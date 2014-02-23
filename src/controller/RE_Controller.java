package controller;

import java.util.ArrayList;
import java.util.HashMap;

import model.CM_Model;
import model.DM_Model;
import model.Login_Model;
import model.RE_Model;
import model.SA_Model;

import org.hibernate.HibernateException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import view.Login_View;
import entity.ExternalUsers;
import entity.InternalUsers;
import entity.criticalTransactions;
import exception.ApplicationException;

@Controller
public class RE_Controller {

	@RequestMapping(value = "/RE_AuthorizationsForm.do")
	public ModelAndView showRequests() {
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
			RE_Model regEmp = new RE_Model();
			ArrayList<criticalTransactions> critTransactions = new ArrayList<criticalTransactions>();
			critTransactions = regEmp
					.getCriticalTransactionsList(UserIDLoggedIn);
			if (critTransactions == null) {
				return new ModelAndView("BadInput");
			}
			currentView.setUserID(UserIDLoggedIn);
			String message = "";
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"RE_ProcessRequest.do\">"

					+ "<br> Select Request <select name=critTransaction /> ";
			for (int i = 0; i < critTransactions.size(); i++) {
				criticalTransactions trans = critTransactions.get(i);
				message += "<option value=" + trans.getAuthId() + " = >"
						+ trans.getAuthId() + "</option>";
			}

			message += "</select><input type=\"submit\" value=\"Submit\"></form>"
					+ "";
			currentView.setMessage(message);
			return new ModelAndView("RE_AuthorizationsForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/RE_ProcessRequest.do")
	public ModelAndView viewParticularRequest(Integer critTransaction) {
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
			RE_Model regEmp = new RE_Model();
			criticalTransactions cTrans = regEmp
					.getCriticalTransaction(critTransaction);
			if (cTrans == null) {
				return new ModelAndView("BadInput");
			}
			currentView.setUserID(UserIDLoggedIn);

			String message = "";
			message += "<p> Customer ID " + cTrans.getAuthGivenby()
					+ "</p><br>";
			message += "<p> Transaction ID " + cTrans.getTransactionId()
					+ "</p><br>";
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"RE_Approve.do\">"

					+ "<input type=\"hidden\" name=\"AuthID\" value="
					+ cTrans.getAuthId()
					+ ">"
					+ "<input type=\"hidden\" name=\"transactionID\" value="
					+ cTrans.getTransactionId() + ">";
			message += "<input type=\"submit\" value=\"Approve\"></form>";

			message += "<form autocomplete=\"off\"  name=form2 method=\"POST\" action=\"RE_EscalationForm.do\">"
					+ "<input type=\"hidden\" name=\"UserIDLoggedIn\" value="
					+ UserIDLoggedIn
					+ ">"
					+ "<input type=\"hidden\" name=\"AuthID\" value="
					+ cTrans.getAuthId()
					+ ">"
					+ "<input type=\"hidden\" name=\"transactionID\" value="
					+ cTrans.getTransactionId() + ">";
			message += "<input type=\"submit\" value=\"Escalate\"></form>" + "";

			currentView.setMessage(message);
			return new ModelAndView("RE_ProcessRequest", "LoginView",
					currentView);
		} catch (Exception ex) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/RE_Approve.do")
	public ModelAndView ApproveTransaction(@RequestParam int AuthID,
			@RequestParam int transactionID) {
		try {
			String UserIDLoggedIn = "";
			try {
				User user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				UserIDLoggedIn = user.getUsername();
			} catch (ClassCastException ex) {
				return new ModelAndView("403");
			}
			RE_Model regEmp = new RE_Model();
			String resultOfDelete = regEmp.deleteCritTransaction(AuthID,
					transactionID);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfDelete);
			return new ModelAndView("RE_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/RE_EscalationForm.do")
	public ModelAndView EscalateRequest(@RequestParam int AuthID,
			@RequestParam int transactionID) {
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
			CM_Model corpMgr = new CM_Model();
			HashMap<String, String> departmentMangerList = corpMgr
					.getListOfTMManagers();
			if (departmentMangerList == null) {
				return new ModelAndView("BadInput");
			}
			currentView.setUserID(UserIDLoggedIn);
			String message = "";
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"RE_ProcessEscalate.do\">"

					+ "<input type=\"hidden\" name=\"AuthID\" value="
					+ AuthID
					+ ">"
					+ "<input type=\"hidden\" name=\"transactionID\" value="
					+ transactionID + ">";
			message += "<select name = \"TM_Manager\" >";
			for (String currentdmID : departmentMangerList.keySet()) {
				message += "<option value = \'" + currentdmID + "\'>"
						+ departmentMangerList.get(currentdmID) + " ("
						+ currentdmID + ")" + "</option>";
			}
			message += "</select><input type=\"submit\" value=\"Escalate\"></form>"
					+ "";
			currentView.setMessage(message);
			return new ModelAndView("RE_EscalationForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/RE_ProcessEscalate.do")
	public ModelAndView EscalateTransaction(@RequestParam int AuthID,
			@RequestParam String TM_Manager) {
		try {

			String UserIDLoggedIn = "";
			try {
				User user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				UserIDLoggedIn = user.getUsername();
			} catch (ClassCastException ex) {
				return new ModelAndView("403");
			}
			RE_Model regEmp = new RE_Model();
			String resultOfDelete = regEmp.addCriticalTransaction(AuthID,
					TM_Manager);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfDelete);
			return new ModelAndView("SAL_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/SAL_UserCreateForm.do")
	public ModelAndView UserCreate() {
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
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"SAL_UserCreateProcess.do\">"
					+ "Username : <input type=\"text\" name=\"Username\">"

					+ "UserType : <select name=\"Usertype\"><option value=\"1\">Individual User</option><option value=\"2\">Merchant</option></select>"
					+ "Name : <input type=\"text\" name=\"fullName\">"
					+ "email : <input type=\"email\" name=\"emailId\">";
			message += "<input type=\"submit\" value=\"Submit\"></form>" + "";
			currentView.setMessage(message);
			return new ModelAndView("RE_UserCreateForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/SAL_UserCreateProcess.do")
	public ModelAndView UserCreateProcess(@RequestParam String Username,
			@RequestParam String fullName, @RequestParam String emailId,
			@RequestParam String Usertype) {
		try {
			if (Login_Model.isInputBad(Username)
					|| Login_Model.isInputBad(fullName)
					|| Login_Model.isInputBad(emailId)
					|| Login_Model.isInputBad(Usertype)) {
				return new ModelAndView("BadInput");
			}
			if (!SA_Model.isValidEmail(emailId)) {
				Login_View view = new Login_View();
				view.setMessage("Email address not supported. Please try with supported email-id versions given in document. ");
				return new ModelAndView("SAL_Home", "LoginView", view);
			}
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
			RE_Model regEmp = new RE_Model();

			String ret = regEmp.requestAdmin(Username, fullName, emailId,
					Usertype, 1);

			String message = ret;
			currentView.setMessage(message);
			return new ModelAndView("SAL_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/SAL_UserModifyForm.do")
	public ModelAndView UserModify() {
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

			RE_Model Reg = new RE_Model();

			ArrayList<ExternalUsers> ExtUsersList = Reg.getExtUsersList();
			if (ExtUsersList == null) {
				return new ModelAndView("BadInput");
			}
			if (ExtUsersList.size() == 0) {
				Login_View v2 = new Login_View();
				v2.setUserID(UserIDLoggedIn);
				v2.setMessage("No users to modify.");
				return new ModelAndView("SAL_Home", "LoginView", v2);
			}
			String message = "";
			message += "<form autocomplete=\"off\"  name = modifyEUsersForm method = \"POST\" "
					+ "action = \"SAL_UserModifyProcess.do\">";
			message += "<select name = \"UserToModify\" >";
			for (ExternalUsers currExUser : ExtUsersList) {
				message += "<option value = \'" + currExUser.getUserId()
						+ "\'>" + currExUser.getUserId() + " ("
						+ currExUser.getUserName() + ")" + "</option>";
			}
			message += "</select><br>";
			message += "<br>Name: <input type = \"text\" name=\"fullName\" value = \"\" />";
			message += "<br>Email: <input type = \"text\" name=\"emailId\" value = \"\" />";
			message += "UserType : <select name=\"Usertype\"><option value=\"1\">Individual User</option><option value=\"2\">Merchant</option></select>";
			message += "<br><input type = submit value = \"Modify\" /></form>"
					+ "";

			currentView.setMessage(message);
			return new ModelAndView("RE_UserModifyForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/SAL_UserModifyProcess")
	public ModelAndView UserModifyProcess(@RequestParam String UserToModify,
			@RequestParam String fullName, @RequestParam String emailId,
			@RequestParam String Usertype) {
		try {
			if (Login_Model.isInputBad(UserToModify)
					|| Login_Model.isInputBad(fullName)
					|| Login_Model.isInputBad(emailId)
					|| Login_Model.isInputBad(Usertype)) {
				return new ModelAndView("BadInput");
			}
			if (!SA_Model.isValidEmail(emailId)) {
				Login_View view = new Login_View();
				view.setMessage("Email address not supported. Please try with supported email-id versions given in document. ");
				return new ModelAndView("SAL_Home", "LoginView", view);
			}
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
			RE_Model regEmp = new RE_Model();

			String ret = regEmp.requestAdmin(UserToModify, fullName, emailId,
					Usertype, 2);

			String message = ret;
			currentView.setMessage(message);
			return new ModelAndView("SAL_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/SAL_UserDeleteForm.do")
	public ModelAndView UserDelete() {
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

			RE_Model Reg = new RE_Model();

			ArrayList<ExternalUsers> ExtUsersList = Reg.getExtUsersList();
			if (ExtUsersList == null) {
				return new ModelAndView("BadInput");
			}
			if (ExtUsersList.size() == 0) {
				Login_View v1 = new Login_View();
				v1.setUserID(UserIDLoggedIn);
				v1.setMessage("No users found. Cannot delete.");
				return new ModelAndView("SAL_Home", "LoginView", v1);
			}
			String message = "";
			message += "<form autocomplete=\"off\"  name = deleteEUsersForm method = \"POST\" "
					+ "action = \"SAL_UserDeleteProcess.do\">";
			message += "<select name = \"UserToDelete\" >";
			for (ExternalUsers currExUser : ExtUsersList) {
				message += "<option value = \'" + currExUser.getUserId()
						+ "\'>" + currExUser.getUserId() + " ("
						+ currExUser.getUserName() + ")" + "</option>";
			}
			message += "</select><br><br><input type = submit value = \"Delete\" /></form>"
					+ "";

			currentView.setMessage(message);
			return new ModelAndView("RE_UserDeleteForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/SAL_UserDeleteProcess.do")
	public ModelAndView UserDeleteProcess(@RequestParam String UserToDelete) {
		try {
			if (Login_Model.isInputBad(UserToDelete)) {
				return new ModelAndView("BadInput");
			}
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
			RE_Model regEmp = new RE_Model();

			String ret = regEmp.requestAdmin(UserToDelete, "default",
					"default", "default", 3);

			String message = ret;
			currentView.setMessage(message);
			return new ModelAndView("SAL_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

}
