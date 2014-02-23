package model;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import Email.EmailService;

import utility.Hibernate_Utility;
import entity.InternalUsers;
import entity.criticalTransactions;
import exception.ApplicationException;

public class DM_Model {

	public String addRegularEmployee(String userId, String userName,
			int departmentOfManager, int designation, String user_email) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String insertInternalUserQuery = "INSERT INTO internal_users (`dept_sales`,`dept_it`,`dept_tm`,`dept_hr`,`dept_cm`, `user_id`, `user_name`, `user_role`, `user_email`) VALUES (";
			if (departmentOfManager == 1)
				insertInternalUserQuery += "1,0,0,0,0";
			if (departmentOfManager == 2)
				insertInternalUserQuery += "0,1,0,0,0";
			if (departmentOfManager == 3)
				insertInternalUserQuery += "0,0,1,0,0";
			if (departmentOfManager == 4)
				insertInternalUserQuery += "0,0,0,1,0";
			if (departmentOfManager == 5)
				insertInternalUserQuery += "0,0,0,0,1";

			insertInternalUserQuery += ", \'" + userId + "\' , \'" + userName
					+ "\' , " + designation + ", \"" + user_email + "\" );";
			SQLQuery query = session.createSQLQuery(insertInternalUserQuery);
			int rowsAffected = query.executeUpdate();
			SecureRandom random = new SecureRandom();
			String randomPwd = new BigInteger(56, random).toString(32);
			if (rowsAffected > 0) {
				String randomPwdHash = DigestUtils.sha1Hex(randomPwd);
				String insertCredentialsQuery = "INSERT INTO credentials (`user_id`, `password_hash`, `otp_done`) VALUES (\'"
						+ userId + "\' , \'" + randomPwdHash + "\' , 0);";

				query = session.createSQLQuery(insertCredentialsQuery);
				rowsAffected = query.executeUpdate();
				String insertAuthorityQuery = "INSERT INTO authority_roles ( `role`, `userid`) VALUES (\"ROLE_"
						+ designation + "\", \"" + userId + "\");";

				query = session.createSQLQuery(insertAuthorityQuery);
				rowsAffected = query.executeUpdate();
				if (rowsAffected > 0) {
					String subject = "Your temporary password from GreenBank Ltd.";

					String body = "Your one time temporary password to login to GreenBank system is \""
							+ randomPwd
							+ "\" \n"
							+ "Please change your password once you login.";
					try {
						EmailService email = new EmailService();
						email.sendEMail(user_email, subject, body);
					} catch (AddressException ex) {
						return "Invalid email address. Please try again with valid email ID.";
					} catch (MessagingException ex) {
						return "Problem with sending email. Please check back later.";
					}
					String commitQuery = "COMMIT;";
					query = session.createSQLQuery(commitQuery);
					rowsAffected = query.executeUpdate();
					return "Employee successfully created!";
				} else {
					return "Failed! Employee could not be created";
				}
			} else {
				return "Failed! Employee creation could not be completed. Please try after sometime.";
			}
		} catch (ConstraintViolationException ex) {
			return "User with the same user ID already exists. Please try a different User ID.";
		} catch (HibernateException e) {
			return "Action Failed! Please try later.";
		} catch (ApplicationException e) {
			return "Action Failed! Please try later.";
		}
	}

	public InternalUsers getDepartments(String userIDLoggedIn) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String getDepartmetnOfCMQuery = "select * from internal_users where user_id = \'"
					+ userIDLoggedIn + "\' ;";
			SQLQuery query = session.createSQLQuery(getDepartmetnOfCMQuery);
			query.addEntity(InternalUsers.class);
			List queryResult = query.list();

			InternalUsers currentUser = (InternalUsers) queryResult.get(0);

			return currentUser;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public HashMap<String, String> getListOfRegularEmployees(int dept) {
		try {
			HashMap<String, String> listofRegularEmployee = new HashMap<String, String>();
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String Sdept = null;
			switch (dept) {
			case 1:
				Sdept = "dept_sales = 1";
				break;
			case 2:
				Sdept = "dept_it = 1";
				break;
			case 3:
				Sdept = "dept_tm = 1";
				break;
			case 4:
				Sdept = "dept_hr = 1";
				break;
			case 5:
				Sdept = "dept_cm = 1";
				break;
			}

			String getDepartmetnOfCMQuery = "select * from internal_users where user_role = 3 and ("
					+ Sdept + ")";
			SQLQuery query = session.createSQLQuery(getDepartmetnOfCMQuery);
			query.addEntity(InternalUsers.class);
			List queryResult = query.list();
			for (Object currentEmp : queryResult) {
				InternalUsers empID = (InternalUsers) currentEmp;

				listofRegularEmployee.put(empID.getUserId(),
						empID.getUserName());

			}
			return listofRegularEmployee;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public String deleteRegularEmployee(String userId) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String deleteInternalUserQuery = "DELETE FROM internal_users WHERE user_id = \'"
					+ userId + "\' and user_role >2 and user_role <7;";
			SQLQuery query = session.createSQLQuery(deleteInternalUserQuery);
			int rowsAffected = query.executeUpdate();

			if (rowsAffected > 0) {
				String deleteCredentialsQuery = "DELETE FROM credentials WHERE user_id = \'"
						+ userId + "\' ;";

				query = session.createSQLQuery(deleteCredentialsQuery);
				rowsAffected = query.executeUpdate();
				String deleteAuthorityQuery = "DELETE FROM authority_roles WHERE userid = \'"
						+ userId + "\' ;";

				query = session.createSQLQuery(deleteAuthorityQuery);
				int rowsAffected2 = query.executeUpdate();
				if (rowsAffected > 0) {
					String commitQuery = "COMMIT;";
					query = session.createSQLQuery(commitQuery);
					rowsAffected = query.executeUpdate();
					return "Employee successfully deleted!";
				} else {
					return "Failed! Employee could not be deleted";
				}
			} else {
				return "Failed! Employee removal could not be completed. Please try after sometime.";
			}
		} catch (HibernateException e) {
			return "Action Failed! Please try later.";
		} catch (ApplicationException e) {
			return "Action Failed! Please try later.";
		}
	}

	public String transferDepartmentManager(String dmToTransfer,
			int deptToTransferDM) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String insertInternalUserQuery = "UPDATE internal_users SET ";
			if (deptToTransferDM == 1)
				insertInternalUserQuery += "dept_cm = 0, dept_hr = 0, dept_it = 0, dept_sales = 1, dept_tm = 0";
			if (deptToTransferDM == 2)
				insertInternalUserQuery += "dept_cm = 0, dept_hr = 0, dept_it = 1, dept_sales = 0, dept_tm = 0";
			if (deptToTransferDM == 3)
				insertInternalUserQuery += "dept_cm = 0, dept_hr = 0, dept_it = 0, dept_sales = 0, dept_tm = 1";
			if (deptToTransferDM == 4)
				insertInternalUserQuery += "dept_cm = 0, dept_hr = 1, dept_it = 0, dept_sales = 0, dept_tm = 0";
			if (deptToTransferDM == 5)
				insertInternalUserQuery += "dept_cm = 1, dept_hr = 0, dept_it = 0, dept_sales = 0, dept_tm = 0";

			insertInternalUserQuery += " WHERE user_id = \'" + dmToTransfer
					+ "\' and user_role >2 and user_role <7 ;";
			SQLQuery query = session.createSQLQuery(insertInternalUserQuery);
			int rowsAffected = query.executeUpdate();

			if (rowsAffected > 0) {
				String commitQuery = "COMMIT;";
				query = session.createSQLQuery(commitQuery);
				rowsAffected = query.executeUpdate();

				return "Transfer of employee was successful.";
			} else {
				return "Failed! Employee transfer could not be completed. Please try after sometime.";
			}
		} catch (HibernateException e) {
			return "Action Failed! Please try later.";
		} catch (ApplicationException e) {
			return "Action Failed! Please try later.";
		}
	}

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
				return "Action Failed! Please check again later.";
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
				return "Successfully escalated!";
			} else {
				return "Cannot Escalate now! Try Later";
			}
		} catch (HibernateException e) {
			return "Action Failed! Please try later.";
		} catch (ApplicationException e) {
			return "Action Failed! Please try later.";
		}
	}

}
