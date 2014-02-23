package controller;

import java.util.List;

import model.SA_Model;

import org.hibernate.HibernateException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import view.Login_View;
import entity.PendingExternalUserRequests;
import exception.ApplicationException;

@Controller
public class SA_Controller {
	@RequestMapping(value = "/SA_CreateExternalUser.do")
	public ModelAndView showCreateRequests() {
		try {
			String UserIDLoggedIn = "";
			try {
				User user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				UserIDLoggedIn = user.getUsername();
			} catch (ClassCastException ex) {
				return new ModelAndView("403");
			}
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);

			String message = "";
			message += "<h2>New External User Requests</h2> <br><br>";
			SA_Model SAModel = new SA_Model();
			List pendingRequests = SAModel.getExternalUserRequests(1);
			if (pendingRequests == null) {
				return new ModelAndView("BadInput");
			}
			message += "<table border = 1>"
					+ "<tr><th>User ID</th><th>User Name</th><th>Take Action</th></tr>";
			for (Object currentRequestObject : pendingRequests) {
				PendingExternalUserRequests currentRequest = (PendingExternalUserRequests) currentRequestObject;
				message += "<tr>"
						+ "<td>"
						+ currentRequest.getUserId()
						+ "</td>"
						+ "<td>"
						+ currentRequest.getUserName()
						+ "</td>"
						+ "<td><form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"SA_PerformAction.do\">"
						+ "<input type = hidden name = requestId value = "
						+ currentRequest.getRequestId()
						+ " >"
						+ "<input type = submit value = \"Create\"></form></td>"
						+ "</tr>";
			}
			message += "</table>" + "";
			currentView.setMessage(message);
			return new ModelAndView("SA_NewSysAdminAction", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}

	}

	@RequestMapping(value = "/SA_ModifyExternalUser.do")
	public ModelAndView showModifyRequests() {
		try {
			String UserIDLoggedIn = "";
			try {
				User user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				UserIDLoggedIn = user.getUsername();
			} catch (ClassCastException ex) {
				return new ModelAndView("403");
			}
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);

			String message = "";
			message += "<h2>New External User Requests</h2> <br><br>";
			SA_Model SAModel = new SA_Model();
			List pendingRequests = SAModel.getExternalUserRequests(2);
			if (pendingRequests == null) {
				return new ModelAndView("BadInput");
			}
			message += "<table border = 1>"
					+ "<tr><th>User ID</th><th>Field to update</th><th>Take Action</th></tr>";
			for (Object currentRequestObject : pendingRequests) {
				String fieldstoUpdate = "";
				PendingExternalUserRequests currentRequest = (PendingExternalUserRequests) currentRequestObject;
				if (!currentRequest.getEmailId().equals("default")) {
					fieldstoUpdate += "EMail-ID, ";
				}
				if (!currentRequest.getUserName().equals("default")) {
					fieldstoUpdate += "Name, ";
				}
				if (currentRequest.getUserType() != 0) {
					fieldstoUpdate += "User Type ";
				}

				message += "<tr>"
						+ "<td>"
						+ currentRequest.getUserId()
						+ "</td>"
						+ "<td>"
						+ fieldstoUpdate
						+ "</td>"
						+ "<td><form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"SA_PerformAction.do\">"
						+ "<input type = hidden name = requestId value = "
						+ currentRequest.getRequestId()
						+ " >"
						+ "<input type = submit value = \"Modify\"></form></td>"
						+ "</tr>";
			}
			message += "</table>" + "";
			currentView.setMessage(message);
			return new ModelAndView("SA_NewSysAdminAction", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/SA_DeleteExternalUser.do")
	public ModelAndView showDeleteRequests() {
		try {
			String UserIDLoggedIn = "";
			try {
				User user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				UserIDLoggedIn = user.getUsername();
			} catch (ClassCastException ex) {
				return new ModelAndView("403");
			}
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);

			String message = "";
			message += "<h2>New External User Requests</h2> <br><br>";
			SA_Model SAModel = new SA_Model();
			List pendingRequests = SAModel.getExternalUserRequests(3);
			if (pendingRequests == null) {
				return new ModelAndView("BadInput");
			}
			message += "<table border = 1>"
					+ "<tr><th>User ID</th><th>Take Action</th></tr>";
			for (Object currentRequestObject : pendingRequests) {
				PendingExternalUserRequests currentRequest = (PendingExternalUserRequests) currentRequestObject;
				message += "<tr>"
						+ "<td>"
						+ currentRequest.getUserId()
						+ "</td>"
						+ "<td><form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"SA_PerformAction.do\">"
						+ "<input type = hidden name = requestId value = "
						+ currentRequest.getRequestId()
						+ " ><input type = submit value = \"Delete\"></form></td>"
						+ "</tr>";
			}
			message += "</table>" + "";
			currentView.setMessage(message);
			return new ModelAndView("SA_NewSysAdminAction", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}

	}

	@RequestMapping(value = "/SA_PerformAction.do")
	public ModelAndView processCreateReuqest(@RequestParam int requestId) {
		try {
			String UserIDLoggedIn = "";
			try {
				User user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				UserIDLoggedIn = user.getUsername();
			} catch (ClassCastException ex) {
				return new ModelAndView("403");
			}
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);

			String message = "";
			SA_Model SAModel = new SA_Model();

			message = SAModel.processPendingRequest(requestId);

			currentView.setMessage(message);

			return new ModelAndView("SA_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

}
