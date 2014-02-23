package model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import utility.Hibernate_Utility;
import entity.InternalUsers;
import entity.ExternalUsers;
import entity.criticalTransactions;
import entity.PendingExternalUserRequests;
import exception.ApplicationException;

public class RE_Model {

	public ArrayList<criticalTransactions> getCriticalTransactionsList(
			String UserId) {
		try {
			ArrayList<criticalTransactions> listofCriticalTransactions = new ArrayList<criticalTransactions>();
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String getcriticalQuery = "select * from critical_transactions_authorizations where auth_given_to = \'"
					+ UserId + "\' ;";
			SQLQuery query = session.createSQLQuery(getcriticalQuery);
			query.addEntity(criticalTransactions.class);
			List queryResult = query.list();
			for (Object critTransaction : queryResult) {
				criticalTransactions cTrans = (criticalTransactions) critTransaction;
				listofCriticalTransactions.add(cTrans);
			}
			return listofCriticalTransactions;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public criticalTransactions getCriticalTransaction(int authId) {
		try {
			ArrayList<criticalTransactions> listofCriticalTransactions = new ArrayList<criticalTransactions>();
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String getcriticalQuery = "select * from critical_transactions_authorizations where auth_id = \'"
					+ authId + "\' ;";
			SQLQuery query = session.createSQLQuery(getcriticalQuery);
			query.addEntity(criticalTransactions.class);
			List queryResult = query.list();

			criticalTransactions cTrans = (criticalTransactions) queryResult
					.get(0);

			return cTrans;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public String deleteCritTransaction(int AuthId, int transactionID) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String deletecritTransQuery = "DELETE FROM critical_transactions_authorizations WHERE auth_id = \'"
					+ AuthId + "\' ;";
			SQLQuery query = session.createSQLQuery(deletecritTransQuery);
			int rowsAffected = query.executeUpdate();

			String TransQuery = "SELECT * FROM critical_transactions_authorizations WHERE transaction_id = \'"
					+ transactionID + "\' ;";
			query = session.createSQLQuery(TransQuery);
			query.addEntity(criticalTransactions.class);
			List queryResult = query.list();

			if (queryResult.size() == 0) {

				String ApproveTransaction = "UPDATE transactions SET transaction_status='Complete'";

				ApproveTransaction += " WHERE transaction_id = "
						+ transactionID + " ;";

				query = session.createSQLQuery(ApproveTransaction);
				rowsAffected = query.executeUpdate();
			}
			if (rowsAffected > 0) {
				String commitQuery = "COMMIT;";
				query = session.createSQLQuery(commitQuery);
				rowsAffected = query.executeUpdate();
				return "Transaction Approved!";
			} else {
				return "Approval Failed!";
			}
		} catch (HibernateException e) {
			return "Action Failed! Please try later.";
		} catch (ApplicationException e) {
			return "Action Failed! Please try later.";
		}

	}

	public String addCriticalTransaction(int authID, String ManagerID) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			criticalTransactions cTrans = getCriticalTransaction(authID);
			if (cTrans == null) {
				return "Action Failed! Please try later.";
			}
			String insertInternalUserQuery = "INSERT INTO critical_transactions_authorizations (`transaction_id`,`auth_given_by`,`auth_given_to`) VALUES ( "
					+ cTrans.getTransactionId()
					+ " , \'"
					+ cTrans.getAuthGivento() + "\' , \'" + ManagerID + "\');";
			SQLQuery query = session.createSQLQuery(insertInternalUserQuery);
			int rowsAffected = query.executeUpdate();
			if (rowsAffected > 0) {
				String commitQuery = "COMMIT;";
				query = session.createSQLQuery(commitQuery);
				rowsAffected = query.executeUpdate();
				return "successfully escalated!";
			} else {
				return "Cannot Escalate now! Try Later";
			}
		} catch (HibernateException e) {
			return "Action Failed! Please try later.";
		} catch (ApplicationException e) {
			return "Action Failed! Please try later.";
		}

	}

	public ArrayList<ExternalUsers> getExtUsersList() {
		try {
			ArrayList<ExternalUsers> listExtUsers = new ArrayList<ExternalUsers>();
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String getcriticalQuery = "select * from external_users";
			SQLQuery query = session.createSQLQuery(getcriticalQuery);
			query.addEntity(ExternalUsers.class);
			List queryResult = query.list();
			for (Object currExtUser : queryResult) {
				ExternalUsers Temp = (ExternalUsers) currExtUser;
				listExtUsers.add(Temp);
			}
			return listExtUsers;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public String requestAdmin(String Username, String Name, String emailID,
			String Usertype, int ReqType) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String checkQuery = null;
			SQLQuery query = null;
			List queryResult = null;
			if (ReqType == 1) {
				checkQuery = "Select * from internal_users where user_id = \""
						+ Username + "\"";
				query = session.createSQLQuery(checkQuery);
				// query.addEntity(PendingExternalUserRequests.class);
				queryResult = query.list();
				if (queryResult.size() > 0) {
					return "Cannot Submit Request!";
				}

				checkQuery = "Select * from external_users where user_id = \""
						+ Username + "\"";
				query = session.createSQLQuery(checkQuery);
				// query.addEntity(PendingExternalUserRequests.class);
				queryResult = query.list();
				if (queryResult.size() > 0) {
					return "Cannot Submit Request!";
				}
			}

			checkQuery = "Select * from pending_external_user_requests where user_id = \""
					+ Username + "\"";
			query = session.createSQLQuery(checkQuery);
			// query.addEntity(PendingExternalUserRequests.class);
			queryResult = query.list();
			if (queryResult.size() > 0) {
				return "Cannot Submit Request!";
			}

			String insertInternalUserQuery = "INSERT INTO pending_external_user_requests (`user_id`,`user_name`,`email_id`,`user_type`,`request_type`) VALUES ( \'"
					+ Username
					+ "\' , \'"
					+ Name
					+ "\' , \'"
					+ emailID
					+ "\' , " + Usertype + " , " + ReqType + ");";
			query = session.createSQLQuery(insertInternalUserQuery);
			int rowsAffected = query.executeUpdate();
			if (rowsAffected > 0) {
				String commitQuery = "COMMIT;";
				query = session.createSQLQuery(commitQuery);
				rowsAffected = query.executeUpdate();
				return "Request successfully Submitted!";
			} else {
				return "Cannot Request for User Create now! Try Later";
			}
		} catch (HibernateException e) {
			return "Action Failed! Please try later.";
		} catch (ApplicationException e) {
			return "Action Failed! Please try later.";
		}
	}

}
