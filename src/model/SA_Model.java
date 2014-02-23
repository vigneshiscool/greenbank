package model;

import java.math.BigInteger;
import java.security.SecureRandom;
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
import entity.ExternalUsers;
import entity.PendingExternalUserRequests;
import exception.ApplicationException;

public class SA_Model {

	public static boolean isValidEmail(String email) {
		if (email.endsWith("@gmail.com") || email.endsWith("@yahoo.com")
				|| email.endsWith("@asu.edu")) {
			return true;
		} else {
			return false;
		}

	}

	public List getExternalUserRequests(int requestType) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String sql = "SELECT * from pending_external_user_requests WHERE request_type = "
					+ requestType + " ;";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(PendingExternalUserRequests.class);
			List results = query.list();
			return results;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public String processPendingRequest(int requestId) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String sql = "SELECT * from pending_external_user_requests WHERE request_id = "
					+ requestId + " ;";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(PendingExternalUserRequests.class);
			List results = query.list();
			PendingExternalUserRequests currentReuqest = (PendingExternalUserRequests) results
					.get(0);
			int action = currentReuqest.getRequestType();
			String returnMessage = "";
			if (action == 1) {
				// Create External User Reuqest
				try {

					sql = "INSERT INTO external_users"
							+ "(`balance_amount`, `email_id`, `user_id`, `user_name`, `user_type`) "
							+ "VALUES (0, \"" + currentReuqest.getEmailId()
							+ "\" , \"" + currentReuqest.getUserId()
							+ "\" , \"" + currentReuqest.getUserName()
							+ "\" , " + currentReuqest.getUserType() + ");";
					query = session.createSQLQuery(sql);
					int rowsAffected = query.executeUpdate();
					SecureRandom random = new SecureRandom();
					String randomPwd = new BigInteger(56, random).toString(32);
					String randomPwdHash = DigestUtils.sha1Hex(randomPwd);
					sql = "INSERT INTO credentials (`user_id`, `password_hash`, `otp_done`) VALUES (\'"
							+ currentReuqest.getUserId()
							+ "\' , \'"
							+ randomPwdHash + "\' , 0);";
					query = session.createSQLQuery(sql);
					int rowsAffected1 = query.executeUpdate();
					int authorityLevel = currentReuqest.getUserType() + 6;
					sql = "INSERT INTO authority_roles ( `role`, `userid`) VALUES (\"ROLE_"
							+ authorityLevel
							+ "\", \""
							+ currentReuqest.getUserId() + "\");";
					query = session.createSQLQuery(sql);
					int rowsAffected4 = query.executeUpdate();
					String subject = "Your temporary password from GreenBank Ltd.";

					String body = "Your one time temporary password to login to GreenBank system is \""
							+ randomPwd
							+ "\" \n"
							+ "Please change your password once you login.";
					try {
						EmailService email = new EmailService();
						email.sendEMail(currentReuqest.getEmailId(), subject,
								body);
					} catch (AddressException ex) {
						return "Invalid email address. Please try again with valid email ID.";
					} catch (MessagingException ex) {
						return "Problem with sending email. Please check back later.";
					}

					sql = "DELETE FROM pending_external_user_requests WHERE request_id = "
							+ currentReuqest.getRequestId() + " ;";
					query = session.createSQLQuery(sql);
					int rowsAffected2 = query.executeUpdate();

					if (rowsAffected == 1 && rowsAffected1 == 1
							&& rowsAffected2 == 1) {
						sql = "COMMIT;";
						query = session.createSQLQuery(sql);
						int rowsAffected3 = query.executeUpdate();
						returnMessage = "User successfully created.";
					} else {
						returnMessage = "Failed! User could not be created. Please try again later.";
					}
				} catch (ConstraintViolationException ex) {
					return "Request could not be processed. User with the same user ID already exists.";
				}
			} else if (action == 2) {
				// Modify External User Reuqest
				sql = "SELECT * from external_users where user_id = \""
						+ currentReuqest.getUserId() + "\" ;";
				query = session.createSQLQuery(sql);
				query.addEntity(ExternalUsers.class);
				ExternalUsers existingUserRecord = (ExternalUsers) query.list()
						.get(0);
				if (!currentReuqest.getUserName().equals("default")) {
					existingUserRecord
							.setUserName(currentReuqest.getUserName());
				}
				if (!currentReuqest.getEmailId().equals("default")) {
					existingUserRecord.setEmail_id(currentReuqest.getEmailId());
				}
				if (currentReuqest.getUserType() != 0) {
					existingUserRecord.setUser_type(currentReuqest
							.getUserType());
				}

				sql = "UPDATE external_users SET `email_id` = \""
						+ existingUserRecord.getEmail_id()
						+ "\" , `user_name` = \""
						+ existingUserRecord.getUserName()
						+ "\" , `user_type` =  "
						+ existingUserRecord.getUser_type()
						+ " WHERE user_id = \""
						+ existingUserRecord.getUserId() + "\" ;";
				query = session.createSQLQuery(sql);
				int rowsAffected1 = query.executeUpdate();

				sql = "DELETE FROM pending_external_user_requests WHERE request_id = "
						+ currentReuqest.getRequestId() + " ;";
				query = session.createSQLQuery(sql);
				int rowsAffected2 = query.executeUpdate();

				if (rowsAffected1 == 1 && rowsAffected2 == 1) {
					sql = "COMMIT;";
					query = session.createSQLQuery(sql);
					int rowsAffected3 = query.executeUpdate();
					returnMessage = "User successfully modified.";
				} else {
					returnMessage = "Failed! User could not be modified. Please try again later.";
				}
			} else if (action == 3) {
				// Delete External User Reuqest - yet to finish
				Login_Model loginModel = new Login_Model();

				if (loginModel.isExternal(currentReuqest.getUserId())) {
					sql = "DELETE FROM external_users WHERE user_id = \""
							+ currentReuqest.getUserId() + "\" ;";
					query = session.createSQLQuery(sql);
					int rowsAffected1 = query.executeUpdate();
					String deleteCredentialsQuery = "DELETE FROM credentials WHERE user_id = \'"
							+ currentReuqest.getUserId() + "\' ;";

					query = session.createSQLQuery(deleteCredentialsQuery);
					int rowsAffectedCredentials = query.executeUpdate();
					String deleteAuthorityQuery = "DELETE FROM authority_roles WHERE userid = \'"
							+ currentReuqest.getUserId() + "\' ;";

					query = session.createSQLQuery(deleteAuthorityQuery);
					int rowsAffectedAuthority = query.executeUpdate();
					sql = "DELETE FROM pending_external_user_requests WHERE request_id = "
							+ currentReuqest.getRequestId() + " ;";
					query = session.createSQLQuery(sql);
					int rowsAffected2 = query.executeUpdate();

					if (rowsAffected1 == 1 && rowsAffectedCredentials == 1
							&& rowsAffected2 == 1) {
						sql = "COMMIT;";
						query = session.createSQLQuery(sql);
						int rowsAffected3 = query.executeUpdate();
						returnMessage = "User successfully deleted.";
					} else {
						returnMessage = "Failed! User could not be deleted. Please try again later.";
					}
				} else {
					returnMessage = "Failed to delete! Please check if delete parameters are correct.";
				}
			}
			return returnMessage;
		} catch (Exception e) {
			return "Action Failed! Please try later.";
		}
	}
}
