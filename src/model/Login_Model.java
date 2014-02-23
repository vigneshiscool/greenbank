package model;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import utility.Hibernate_Utility;
import entity.Credentials;
import entity.ExternalUsers;
import entity.InternalUsers;
import exception.ApplicationException;

public class Login_Model {

	public static boolean isInputBad(String input) {
		if (input.contains("IN-VALID-GREENBANK") || input.length() == 0
				|| input.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public InternalUsers getInternalUser(String UserID)
			throws HibernateException, ApplicationException {
		Session session = Hibernate_Utility.getSessionFactory().openSession();
		String sql = "SELECT * FROM internal_users WHERE user_id = :user_id";
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(InternalUsers.class);
		query.setParameter("user_id", UserID);
		List results = query.list();
		InternalUsers loggedInUser = (InternalUsers) results.get(0);
		session.close();

		return loggedInUser;
	}

	public boolean validatePassword(String UserID, String Password)
			throws HibernateException, ApplicationException {
		Session session = Hibernate_Utility.getSessionFactory().openSession();

		String sql1 = "SELECT * FROM credentials WHERE user_id = :user_id";
		SQLQuery query1 = session.createSQLQuery(sql1);
		query1.addEntity(Credentials.class);
		query1.setParameter("user_id", UserID);
		List credentialsResults = query1.list();

		if (credentialsResults.size() >= 1) {
			Credentials userCredentials = (Credentials) credentialsResults
					.get(0);
			if (userCredentials.getUserId().equalsIgnoreCase(UserID)
					&& userCredentials.getPasswordHash().equals(Password)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	public Boolean isInternal(String userID) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String sql = "SELECT * FROM internal_users WHERE user_id = :user_id";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(InternalUsers.class);
			query.setParameter("user_id", userID);
			List results = query.list();
			session.close();

			if (results.size() > 0)
				return true;
			else
				return false;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public Boolean isExternal(String userID) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String sql = "SELECT * FROM external_users WHERE user_id = \'"
					+ userID + "\' ;";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(ExternalUsers.class);
			List results = query.list();
			session.close();

			if (results.size() > 0)
				return true;
			else
				return false;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public ExternalUsers getExternalUser(String UserID) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String sql = "SELECT * FROM external_users WHERE user_id = \'"
					+ UserID + "\' ;";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(ExternalUsers.class);
			List results = query.list();
			ExternalUsers loggedInUser = (ExternalUsers) results.get(0);
			session.close();

			return loggedInUser;
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public Boolean isOTPSet(String userID) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String sql = "SELECT * FROM credentials WHERE user_id = \""
					+ userID + "\" ;";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Credentials.class);
			List results = query.list();
			session.close();
			if (((Credentials) results.get(0)).getOtpDone() == 1) {
				return true;
			} else {
				return false;
			}
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

	public Boolean setOTP(String userID, String password) {
		try {
			Session session = Hibernate_Utility.getSessionFactory()
					.openSession();
			String sql = "UPDATE credentials SET `otp_done` = 1, `password_hash` = \""
					+ password + "\" WHERE user_id = \"" + userID + "\" ;";
			SQLQuery query = session.createSQLQuery(sql);
			int rowsUpdated = query.executeUpdate();

			if (rowsUpdated == 1) {
				sql = "COMMIT ;";
				query = session.createSQLQuery(sql);
				rowsUpdated = query.executeUpdate();
				return true;
			} else {
				return false;
			}
		} catch (HibernateException e) {
			return null;
		} catch (ApplicationException e) {
			return null;
		}
	}

}
