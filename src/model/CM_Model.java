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

public class CM_Model {

	public String addNewDepartmentManager(String userId, String userName,
			int departmentOfManager, int designation, String user_email) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			try {
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

				insertInternalUserQuery += ", \'" + userId + "\' , \'"
						+ userName + "\' , " + designation + ", \""
						+ user_email + "\" );";
				SQLQuery query = session
						.createSQLQuery(insertInternalUserQuery);
				int rowsAffected = query.executeUpdate();
				SecureRandom random = new SecureRandom();
				String randomPwd = new BigInteger(56, random).toString(32);
				if (rowsAffected > 0) {
					String randomPwdHash = DigestUtils.sha1Hex(randomPwd);
					String insertCredentialsQuery = "INSERT INTO credentials (`user_id`, `password_hash`, `otp_done`) VALUES (\'"
							+ userId + "\' , \'" + randomPwdHash + "\' , 0);";

					query = session.createSQLQuery(insertCredentialsQuery);
					rowsAffected = query.executeUpdate();

					String insertAuthRoles = "INSERT INTO authority_roles ( `role`, `userid`) VALUES (\"ROLE_"
							+ designation + "\", \"" + userId + "\");";

					query = session.createSQLQuery(insertAuthRoles);
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
						return "Department manager successfully created!";
					} else {
						return "Failed! Department manager could not be created";
					}
				} else {
					return "Failed! Department manager creation could not be completed. Please try after sometime.";
				}
			} catch (ConstraintViolationException ex) {
				return "User with the same user ID already exists. Please try a different User ID.";
			}
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

	public HashMap<String, String> getListOfDepartementMangers(
			ArrayList<Integer> deptList) {
		try {
			HashMap<String, String> listOfDepartmentMangers = new HashMap<String, String>();
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String getDepartmetnOfCMQuery = "select * from internal_users where user_role = 2 and (";
			if (deptList.contains(1))
				getDepartmetnOfCMQuery += " dept_sales = 1 or";
			if (deptList.contains(2))
				getDepartmetnOfCMQuery += " dept_it = 1 or";
			if (deptList.contains(3))
				getDepartmetnOfCMQuery += " dept_tm = 1 or";
			if (deptList.contains(4))
				getDepartmetnOfCMQuery += " dept_hr = 1 or";
			if (deptList.contains(5))
				getDepartmetnOfCMQuery += " dept_cm = 1 or";
			getDepartmetnOfCMQuery += " user_role != 2) ;";
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

	public HashMap<String, String> getListOfTMManagers() {
		try {
			HashMap<String, String> listOfDepartmentMangers = new HashMap<String, String>();
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String getDepartmetnOfCMQuery = "select * from internal_users where user_role = 2 and dept_tm = 1";
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

	public HashMap<String, String> getListOfCorpManagers() {
		try {
			HashMap<String, String> listofCorpManagers = new HashMap<String, String>();
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String getDepartmetnOfCMQuery = "select * from internal_users where user_role = 1 and dept_tm = 1";
			SQLQuery query = session.createSQLQuery(getDepartmetnOfCMQuery);
			query.addEntity(InternalUsers.class);
			List queryResult = query.list();
			for (Object currCorpMgr : queryResult) {
				InternalUsers dmID = (InternalUsers) currCorpMgr;
				listofCorpManagers.put(dmID.getUserId(), dmID.getUserName());
			}
			return listofCorpManagers;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public String deleteDepartmentManager(String userId) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();

			String deleteInternalUserQuery = "DELETE FROM internal_users WHERE user_id = \'"
					+ userId + "\' and user_role=2;";
			SQLQuery query = session.createSQLQuery(deleteInternalUserQuery);
			int rowsAffected = query.executeUpdate();

			if (rowsAffected > 0) {
				String deleteCredentialsQuery = "DELETE FROM credentials WHERE user_id = \'"
						+ userId + "\' ;";

				query = session.createSQLQuery(deleteCredentialsQuery);
				int rowsAffected1 = query.executeUpdate();
				String deleteAuthorityQuery = "DELETE FROM authority_roles WHERE userid = \'"
						+ userId + "\' ;";

				query = session.createSQLQuery(deleteAuthorityQuery);
				int rowsAffected2 = query.executeUpdate();
				if (rowsAffected1 > 0) {
					String commitQuery = "COMMIT;";
					query = session.createSQLQuery(commitQuery);
					rowsAffected = query.executeUpdate();
					return "Department manager successfully deleted!";
				} else {
					return "Failed! Department manager could not be deleted";
				}
			} else {
				return "Failed! Department manager removal could not be completed. Please try after sometime.";
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
					+ "\' and user_role = 2;";
			SQLQuery query = session.createSQLQuery(insertInternalUserQuery);
			int rowsAffected = query.executeUpdate();

			if (rowsAffected > 0) {
				String commitQuery = "COMMIT;";
				query = session.createSQLQuery(commitQuery);
				rowsAffected = query.executeUpdate();

				return "Transfer of department manager was successful.";
			} else {
				return "Failed! Department manager transfer could not be completed. Please try after sometime.";
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
				return "Action Failed! Please try again later.";
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

}
