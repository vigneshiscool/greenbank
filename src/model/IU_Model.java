package model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import utility.Hibernate_Utility;
import entity.ExternalUsers;
import entity.InternalUsers;
import entity.Transactions;
import exception.ApplicationException;

public class IU_Model {

	public String createNewNormalTransaction(String UserIDLoggedIn,
			String transaction_from, String transaction_time,
			double transaction_amount, String transaction_description,
			String transaction_to, int transaction_type, String employee_name) {
		try {
			String message;
			String transaction_status = "";
			if (transaction_type == 3
					&& getExternalUser(transaction_from).getUser_type() == 1
					&& getExternalUser(transaction_to).getUser_type() == 2) {
				transaction_amount = transaction_amount * -1;
				transaction_status = "Pending";
				Session session = Hibernate_Utility.getSessionFactory()
						.openSession();
				String sql = "INSERT INTO transactions(`transaction_amount`,`transaction_datetime`,`transaction_description`,`transaction_from`,`transaction_status`,`transaction_to`) VALUES ("
						+ transaction_amount
						+ ",\""
						+ transaction_time
						+ "\",\""
						+ transaction_description
						+ "\", \""
						+ transaction_from
						+ " \",\""
						+ transaction_status
						+ "\",\"" + transaction_to + "\");";
				SQLQuery query = session.createSQLQuery(sql);
				int rowsAffected = query.executeUpdate();
				sql = "SELECT LAST_INSERT_ID();";
				query = session.createSQLQuery(sql);
				int lastTransactionID = ((BigInteger) query.list().get(0))
						.intValue();
				sql = "INSERT INTO pending_merchant_payments"
						+ "(`customer_user_id`, `merchant_user_id`, `transaction_id`) "
						+ "VALUES (\"" + transaction_from + "\" , \""
						+ transaction_to + "\" , " + lastTransactionID + ");";
				query = session.createSQLQuery(sql);
				int pendingPaymentResult = query.executeUpdate();
				if (rowsAffected == 1 && pendingPaymentResult == 1) {
					sql = "COMMIT;";
					query = session.createSQLQuery(sql);
					pendingPaymentResult = query.executeUpdate();
					message = "Transfer successful. Wait for merchant to process payment";
				} else {
					message = "Transfer failed. Please try again later";
				}

			} else {

				if (transaction_amount > (double) 1000) {

					transaction_status = "Pending";
				} else {
					transaction_status = "Success";
				}
				if (transaction_type == 2 || transaction_type == 3) {
					transaction_amount *= -1;
				}

				Session session = Hibernate_Utility.getSessionFactory()
						.openSession();
				String sql = "INSERT INTO transactions(`transaction_amount`,`transaction_datetime`,`transaction_description`,`transaction_from`,`transaction_status`,`transaction_to`) VALUES ("
						+ transaction_amount
						+ ",\""
						+ transaction_time
						+ "\",\""
						+ transaction_description
						+ "\", \""
						+ transaction_from
						+ " \",\""
						+ transaction_status
						+ "\",\"" + transaction_to + "\");";
				SQLQuery query = session.createSQLQuery(sql);
				int rowsAffected = query.executeUpdate();
				int approvalRowsAffected;

				int lastTransactionID;
				Double existingBalance;
				double newBalance;
				if (rowsAffected == 1) {

					if (Math.abs(transaction_amount) < (double) 1000) {
						existingBalance = getBalance(transaction_from);
						if (existingBalance == null) {
							return "Action failed! Please try again later.";
						}
						newBalance = existingBalance + transaction_amount;
						sql = "UPDATE external_users SET balance_amount = "
								+ newBalance + " WHERE user_id = \""
								+ UserIDLoggedIn + "\" ;";
						query = session.createSQLQuery(sql);
						rowsAffected = query.executeUpdate();
					} else {
						sql = "SELECT LAST_INSERT_ID();";
						query = session.createSQLQuery(sql);
						lastTransactionID = ((BigInteger) query.list().get(0))
								.intValue();

						sql = "INSERT INTO critical_transactions_authorizations"
								+ "(`auth_given_by`, `auth_given_to`, `transaction_id`) "
								+ "VALUES (\""
								+ UserIDLoggedIn
								+ "\" , \""
								+ employee_name
								+ "\" , "
								+ lastTransactionID
								+ ");";
						query = session.createSQLQuery(sql);
						approvalRowsAffected = query.executeUpdate();
					}
					int receiverRowsAffected = 1;
					if (transaction_type == 3
							&& Math.abs(transaction_amount) < (double) 1000) {
						sql = "SELECT * from external_users WHERE user_id = \""
								+ transaction_to + "\"";
						query = session.createSQLQuery(sql);
						query.addEntity(ExternalUsers.class);
						List results = query.list();
						if (results.size() == 0
								|| transaction_from.equals(transaction_to)) {
							message = "Invalid recepient. Please enter a valid user id to finish the transfer.";

						}

						existingBalance = getBalance(transaction_to);
						if (existingBalance == null) {
							return "Action failed! Please try again later.";
						}
						double manipulatedBalance = transaction_amount * -1;
						newBalance = existingBalance + manipulatedBalance;
						sql = "UPDATE external_users SET balance_amount = "
								+ newBalance + " WHERE user_id = \""
								+ transaction_to + "\" ;";
						query = session.createSQLQuery(sql);
						receiverRowsAffected = query.executeUpdate();
					}

					if (rowsAffected == 1 && receiverRowsAffected == 1) {
						sql = "COMMIT;";
						query = session.createSQLQuery(sql);
						rowsAffected = query.executeUpdate();
						message = "Transaction was successful.";

					} else {
						message = "Failed! Transaction could not be completed. Please try later..";
					}
				} else {
					message = "Failed! Transaction could not be completed. Please try later..";
				}
			}
			return message;
		} catch (HibernateException e) {
			return "Action Failed! Please try later.";
		} catch (ApplicationException e) {
			return "Action Failed! Please try later.";
		} catch (Exception ex) {
			return "Action Failed! Please try later.";
		}

	}

	private Double getBalance(String userIDLoggedIn) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String sql = "SELECT * FROM external_users WHERE user_id = \""
					+ userIDLoggedIn + "\" ;";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(ExternalUsers.class);
			List results = query.list();
			double balance_amount = (double) ((ExternalUsers) results.get(0))
					.getBalance_amount();
			session.close();
			return balance_amount;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public ExternalUsers getAccountDetails(String userIDLoggedIn) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String sql = "SELECT * from external_users WHERE user_id = \""
					+ userIDLoggedIn + "\"";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(ExternalUsers.class);
			List results = query.list();
			if (results.size() == 0) {
				return null;
			} else {
				ExternalUsers currentUser = (ExternalUsers) results.get(0);
				return currentUser;
			}
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public List getTransactions(String userIDLoggedIn) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String sql = "SELECT * from transactions WHERE ( transaction_from = \""
					+ userIDLoggedIn
					+ "\" OR transaction_to = \""
					+ userIDLoggedIn + "\" ) ;";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Transactions.class);
			List results = query.list();
			return results;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public HashMap<String, String> getListOfRegularEmployees() {
		try {
			HashMap<String, String> listOfDepartmentMangers = new HashMap<String, String>();
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String getDepartmetnOfCMQuery = "select * from internal_users where user_role = 5 and dept_tm = 1 ;";
			SQLQuery query = session.createSQLQuery(getDepartmetnOfCMQuery);
			query.addEntity(InternalUsers.class);
			List queryResult = query.list();
			for (Object currentDM : queryResult) {
				InternalUsers dmID = (InternalUsers) currentDM;

				listOfDepartmentMangers.put(dmID.getUserId(),
						dmID.getUserName());

			}
			return listOfDepartmentMangers;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public ExternalUsers getExternalUser(String userId) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String getDepartmetnOfCMQuery = "select * from external_users where user_id = \""
					+ userId + "\" ;";
			SQLQuery query = session.createSQLQuery(getDepartmetnOfCMQuery);
			query.addEntity(ExternalUsers.class);
			ExternalUsers currentUser = ((ExternalUsers) query.list().get(0));
			return currentUser;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

}
