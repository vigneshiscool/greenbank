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

import validator.LoginValidator;
import view.Login_View;
import entity.InternalUsers;
import entity.criticalTransactions;
import exception.ApplicationException;

@Controller
public class DM_Controller {

	@RequestMapping(value = "/DM_newRegularEmployeeForm.do")
	public ModelAndView showNewRegularEmployeeForm() {
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
			DM_Model DMgrModel = new DM_Model();
			InternalUsers currentMgr = DMgrModel.getDepartments(UserIDLoggedIn);
			if (currentMgr == null) {
				return new ModelAndView("BadInput");
			}
			int RegEmployeeDept = 0;
			if (currentMgr.getDept_sales() == 1)
				RegEmployeeDept = 1;
			if (currentMgr.getDept_it() == 1)
				RegEmployeeDept = 2;
			if (currentMgr.getDept_tm() == 1)
				RegEmployeeDept = 3;
			if (currentMgr.getDept_hr() == 1)
				RegEmployeeDept = 4;
			if (currentMgr.getDept_cm() == 1)
				RegEmployeeDept = 5;

			currentView.setUserID(UserIDLoggedIn);
			String message = "";
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"DM_CreateNewRegularEmployee.do\">"

					+ "<input type=\"hidden\" name=\"deptIdOfEmp\" value="
					+ RegEmployeeDept
					+ ">"
					+ "<br> Preferred UserID <input type=text name=userId /> "
					+ "<br> Email <input type=email name=user_email />"
					+ "<br>Name <input	type=text name=userName /> ";
			message += "</select><input type=\"submit\" value=\"Add\"></form>"
					+ "";
			currentView.setMessage(message);
			return new ModelAndView("DM_newRegularEmployeeForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/DM_CreateNewRegularEmployee.do")
	public ModelAndView createNewRegularEmployee(@RequestParam String userId,
			@RequestParam String userName, @RequestParam int deptIdOfEmp,
			@RequestParam String user_email) {
		try {
			if (Login_Model.isInputBad(userId)
					|| Login_Model.isInputBad(userName)
					|| Login_Model.isInputBad(user_email)) {
				return new ModelAndView("BadInput");
			}
			if (!SA_Model.isValidEmail(user_email)) {
				Login_View view = new Login_View();
				view.setMessage("Email address not supported. Please try with supported email-id versions given in document. ");
				return new ModelAndView("DM_Home", "LoginView", view);
			}
			String UserIDLoggedIn = "";
			try {
				User user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				UserIDLoggedIn = user.getUsername();
			} catch (ClassCastException ex) {
				return new ModelAndView("403");
			}
			DM_Model Mgr = new DM_Model();
			int user_role = 3;
			if (deptIdOfEmp == 4 || deptIdOfEmp == 5) {
				user_role = 3;
			} else if (deptIdOfEmp == 1) {
				user_role = 4;
			} else if (deptIdOfEmp == 2) {
				user_role = 6;
			} else if (deptIdOfEmp == 3) {
				user_role = 5;
			}
			String resultOfAdd = Mgr.addRegularEmployee(userId, userName,
					deptIdOfEmp, user_role, user_email);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfAdd);
			return new ModelAndView("DM_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/DM_DeleteRegularEmployeeForm.do")
	public ModelAndView showDepartmentManagerToDeleteForm() {
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
			DM_Model DMgrModel = new DM_Model();
			InternalUsers currentMgr = DMgrModel.getDepartments(UserIDLoggedIn);
			if (currentMgr == null) {
				return new ModelAndView("BadInput");
			}
			int RegEmployeeDept = 0;
			if (currentMgr.getDept_sales() == 1)
				RegEmployeeDept = 1;
			if (currentMgr.getDept_it() == 1)
				RegEmployeeDept = 2;
			if (currentMgr.getDept_tm() == 1)
				RegEmployeeDept = 3;
			if (currentMgr.getDept_hr() == 1)
				RegEmployeeDept = 4;
			if (currentMgr.getDept_cm() == 1)
				RegEmployeeDept = 5;

			HashMap<String, String> RegEmpList = DMgrModel
					.getListOfRegularEmployees(RegEmployeeDept);
			if (RegEmpList == null) {
				return new ModelAndView("BadInput");
			}
			if (RegEmpList.size() == 0) {
				Login_View view1 = new Login_View();
				view1.setUserID(UserIDLoggedIn);
				view1.setMessage("No employees under your control to delete.");
				return new ModelAndView("DM_Home", "LoginView", view1);
			}
			message += "Select an Employee to delete <form autocomplete=\"off\"  name = deleteEmpForm method = \"POST\" "
					+ "action = \"DM_DeleteRegularEmployee.do\">";
			message += "<select name = \"EmpToDelete\" >";
			for (String currentEmpID : RegEmpList.keySet()) {
				message += "<option value = \'" + currentEmpID + "\'>"
						+ RegEmpList.get(currentEmpID) + " (" + currentEmpID
						+ ")" + "</option>";
			}
			message += "</select><br><br><input type = submit value = \"Delete\" /></form>"
					+ "";
			currentView.setMessage(message);
			return new ModelAndView("DM_DeleteRegularEmployeeForm",
					"LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/DM_DeleteRegularEmployee.do")
	public ModelAndView deleteRegularEmployee(@RequestParam String EmpToDelete) {
		try {
			if (Login_Model.isInputBad(EmpToDelete)) {
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
			DM_Model Mgr = new DM_Model();
			String resultOfDelete = Mgr.deleteRegularEmployee(EmpToDelete);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfDelete);
			return new ModelAndView("DM_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/DM_AuthorizationsForm.do")
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
			DM_Model Mgr = new DM_Model();
			ArrayList<criticalTransactions> critTransactions = new ArrayList<criticalTransactions>();
			critTransactions = Mgr.getCriticalTransactionsList(UserIDLoggedIn);
			if (critTransactions == null) {
				return new ModelAndView("BadInput");
			}
			currentView.setUserID(UserIDLoggedIn);
			String message = "";
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"DM_ProcessRequest.do\">"

					+ "<br> Select Request <select name=critTransaction /> ";
			for (int i = 0; i < critTransactions.size(); i++) {
				criticalTransactions trans = critTransactions.get(i);
				message += "<option value=" + trans.getAuthId() + " = >"
						+ trans.getAuthId() + "</option>";
			}

			message += "</select><input type=\"submit\" value=\"Submit\"></form>"
					+ "";
			currentView.setMessage(message);
			return new ModelAndView("DM_AuthorizationsForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/DM_ProcessRequest.do")
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
			DM_Model Mgr = new DM_Model();
			criticalTransactions cTrans = Mgr
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
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"DM_Approve.do\">"

					+ "<input type=\"hidden\" name=\"AuthID\" value="
					+ cTrans.getAuthId()
					+ ">"
					+ "<input type=\"hidden\" name=\"transactionID\" value="
					+ cTrans.getTransactionId() + ">";
			message += "<input type=\"submit\" value=\"Approve\"></form>";

			message += "<form autocomplete=\"off\"  name=form2 method=\"POST\" action=\"DM_EscalationForm.do\">"
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
			return new ModelAndView("DM_ProcessRequest", "LoginView",
					currentView);
		} catch (Exception ex) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/DM_Approve.do")
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

			DM_Model Mgr = new DM_Model();
			String resultOfDelete = Mgr.deleteCritTransaction(AuthID,
					transactionID);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfDelete);
			return new ModelAndView("DM_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/DM_EscalationForm.do")
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
			HashMap<String, String> corpMgrList = corpMgr
					.getListOfCorpManagers();
			if (corpMgrList == null) {
				return new ModelAndView("BadInput");
			}
			currentView.setUserID(UserIDLoggedIn);
			String message = "";
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"DM_ProcessEscalate.do\">"

					+ "<input type=\"hidden\" name=\"AuthID\" value="
					+ AuthID
					+ ">"
					+ "<input type=\"hidden\" name=\"transactionID\" value="
					+ transactionID + ">";
			message += "<select name = \"TM_CorpManager\" >";
			for (String currentdmID : corpMgrList.keySet()) {
				message += "<option value = \'" + currentdmID + "\'>"
						+ corpMgrList.get(currentdmID) + " (" + currentdmID
						+ ")" + "</option>";
			}
			message += "</select><input type=\"submit\" value=\"Escalate\"></form>"
					+ "";
			currentView.setMessage(message);
			return new ModelAndView("DM_EscalationForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/DM_ProcessEscalate.do")
	public ModelAndView EscalateTransaction(@RequestParam int AuthID,
			@RequestParam String TM_CorpManager) {
		try {
			if (Login_Model.isInputBad(TM_CorpManager)) {
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

			DM_Model dMgr = new DM_Model();
			String resultOfDelete = dMgr.addCriticalTransaction(AuthID,
					TM_CorpManager);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfDelete);
			return new ModelAndView("DM_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

}
