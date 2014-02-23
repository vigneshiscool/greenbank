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
import entity.InternalUsers;
import entity.criticalTransactions;
import exception.ApplicationException;

@Controller
public class CM_Controller {

	@RequestMapping(value = "/CM_newDepartmentManagerForm.do")
	public ModelAndView showNewDepartmentManagerForm() {
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
			CM_Model corporateModel = new CM_Model();
			InternalUsers currentCM = corporateModel
					.getDepartments(UserIDLoggedIn);
			if (currentCM == null) {
				return new ModelAndView("BadInput");
			}
			ArrayList<Integer> deptList = new ArrayList<Integer>();
			if (currentCM.getDept_sales() == 1)
				deptList.add(1);
			if (currentCM.getDept_it() == 1)
				deptList.add(2);
			if (currentCM.getDept_tm() == 1)
				deptList.add(3);
			if (currentCM.getDept_hr() == 1)
				deptList.add(4);
			if (currentCM.getDept_cm() == 1)
				deptList.add(5);

			currentView.setUserID(UserIDLoggedIn);
			String message = "";
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"CM_CreateNewDepartmentManager.do\">"

					+ "<br> Preferred UserID <input autocomplete=\"off\" type=text name=userId /> "
					+ "<br> Email ID <input autocomplete=\"off\" type=email name=user_email />"
					+ "Name <input	type=text name=userName /> "
					+ "<select name = deptIdOfDM>";
			if (deptList.contains(1))
				message += "<option value = 1>Sales</option>";
			if (deptList.contains(2))
				message += "<option value = 2>IT and Technical Support</option>";
			if (deptList.contains(3))
				message += "<option value = 3>Transactions Monitoring</option>";
			if (deptList.contains(4))
				message += "<option value = 4>Human Resources</option>";
			if (deptList.contains(5))
				message += "<option value = 5>Company Management</option>";
			message += "</select><input autocomplete=\"off\" type=\"submit\" value=\"Add\"></form>"
					+ "";
			currentView.setMessage(message);
			return new ModelAndView("CM_NewDepartmentManagerForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/CM_CreateNewDepartmentManager.do")
	public ModelAndView createNewDepartmentManager(@RequestParam String userId,
			@RequestParam String userName, @RequestParam int deptIdOfDM,
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
				return new ModelAndView("CM_Home", "LoginView", view);
			}
			String UserIDLoggedIn = "";
			try {
				User user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				UserIDLoggedIn = user.getUsername();
			} catch (ClassCastException ex) {
				return new ModelAndView("403");
			}
			CM_Model corporateModel = new CM_Model();

			String resultOfAdd = corporateModel.addNewDepartmentManager(userId,
					userName, deptIdOfDM, 2, user_email);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfAdd);
			return new ModelAndView("CM_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/CM_deleteDepartmentManagerForm.do")
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
			CM_Model corporateModel = new CM_Model();
			InternalUsers currentCM = corporateModel
					.getDepartments(UserIDLoggedIn);
			if (currentCM == null) {
				return new ModelAndView("BadInput");
			}
			ArrayList<Integer> deptList = new ArrayList<Integer>();
			if (currentCM.getDept_sales() == 1)
				deptList.add(1);
			if (currentCM.getDept_it() == 1)
				deptList.add(2);
			if (currentCM.getDept_tm() == 1)
				deptList.add(3);
			if (currentCM.getDept_hr() == 1)
				deptList.add(4);
			if (currentCM.getDept_cm() == 1)
				deptList.add(5);
			HashMap<String, String> departmentMangerList = corporateModel
					.getListOfDepartementMangers(deptList);
			if (departmentMangerList == null) {
				return new ModelAndView("BadInput");
			}
			if (departmentMangerList.size() == 0) {
				Login_View view1 = new Login_View();
				view1.setUserID(UserIDLoggedIn);
				view1.setMessage("No employees under your control to delete.");
				return new ModelAndView("CM_Home", "LoginView", view1);
			}
			message += "Select a department manager to delete <form autocomplete=\"off\"  name = deleteDMForm method = \"POST\" "
					+ "action = \"CM_DeleteDepartmentManager.do\">";
			message += "<select name = \"dmToDelete\" >";
			for (String currentdmID : departmentMangerList.keySet()) {
				message += "<option value = \'" + currentdmID + "\'>"
						+ departmentMangerList.get(currentdmID) + " ("
						+ currentdmID + ")" + "</option>";
			}
			message += "</select><br><br><input type = submit value = \"Delete\" /></form>";
			currentView.setMessage(message);
			return new ModelAndView("CM_DeleteDepartmentManagerForm",
					"LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/CM_DeleteDepartmentManager.do")
	public ModelAndView deleteDepartmentManager(@RequestParam String dmToDelete) {
		try {
			if (Login_Model.isInputBad(dmToDelete)) {
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
			CM_Model corporateModel = new CM_Model();
			String resultOfDelete = corporateModel
					.deleteDepartmentManager(dmToDelete);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfDelete);
			return new ModelAndView("CM_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/CM_transferDepartmentManagerForm.do")
	public ModelAndView showDepartmentManagerToTransferForm() {
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
			CM_Model corporateModel = new CM_Model();
			InternalUsers currentCM = corporateModel
					.getDepartments(UserIDLoggedIn);
			if (currentCM == null) {
				return new ModelAndView("BadInput");
			}
			ArrayList<Integer> deptList = new ArrayList<Integer>();
			if (currentCM.getDept_sales() == 1)
				deptList.add(1);
			if (currentCM.getDept_it() == 1)
				deptList.add(2);
			if (currentCM.getDept_tm() == 1)
				deptList.add(3);
			if (currentCM.getDept_hr() == 1)
				deptList.add(4);
			if (currentCM.getDept_cm() == 1)
				deptList.add(5);
			HashMap<String, String> departmentMangerList = corporateModel
					.getListOfDepartementMangers(deptList);
			if (departmentMangerList == null) {
				return new ModelAndView("BadInput");
			}
			message += "Select a department manager and target department <form autocomplete=\"off\"  name = deleteDMForm method = \"POST\" "
					+ "action = \"CM_TransferDepartmentManager.do\">";
			message += "<select name = \"dmToTransfer\" >";
			for (String currentdmID : departmentMangerList.keySet()) {
				message += "<option value = \'" + currentdmID + "\'>"
						+ departmentMangerList.get(currentdmID) + " ("
						+ currentdmID + ")" + "</option>";
			}
			message += " <br></select><br><br>"
					+ "<select name = deptToTransferDM>";
			if (deptList.contains(1))
				message += "<option value = 1>Sales</option>";
			if (deptList.contains(2))
				message += "<option value = 2>IT and Technical Support</option>";
			if (deptList.contains(3))
				message += "<option value = 3>Transactions Monitoring</option>";
			if (deptList.contains(4))
				message += "<option value = 4>Human Resources</option>";
			if (deptList.contains(5))
				message += "<option value = 5>Company Management</option>";
			message += "</select><br><br><br><input type = submit value = \"Transfer\" /></form>";
			currentView.setMessage(message);
			return new ModelAndView("CM_DeleteDepartmentManagerForm",
					"LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/CM_TransferDepartmentManager.do")
	public ModelAndView transferDepartmentManager(
			@RequestParam String dmToTransfer,
			@RequestParam int deptToTransferDM) {
		try {
			if (Login_Model.isInputBad(dmToTransfer)) {
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
			CM_Model corporateModel = new CM_Model();
			String resultOfUpdate = corporateModel.transferDepartmentManager(
					dmToTransfer, deptToTransferDM);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfUpdate);
			return new ModelAndView("CM_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/CM_AuthorizationsForm.do")
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
			CM_Model Mgr = new CM_Model();
			ArrayList<criticalTransactions> critTransactions = new ArrayList<criticalTransactions>();
			critTransactions = Mgr.getCriticalTransactionsList(UserIDLoggedIn);
			if (critTransactions == null) {
				return new ModelAndView("BadInput");
			}
			currentView.setUserID(UserIDLoggedIn);
			String message = "";
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"CM_ProcessRequest.do\">"

					+ "<br> Select Request <select name=critTransaction /> ";
			for (int i = 0; i < critTransactions.size(); i++) {
				criticalTransactions trans = critTransactions.get(i);
				message += "<option value=" + trans.getAuthId() + " = >"
						+ trans.getAuthId() + "</option>";
			}

			message += "</select><input type=\"submit\" value=\"Submit\"></form>";
			currentView.setMessage(message);
			return new ModelAndView("CM_AuthorizationsForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/CM_ProcessRequest.do")
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
			CM_Model Mgr = new CM_Model();
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
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"CM_Approve.do\">"

					+ "<input type=\"hidden\" name=\"AuthID\" value="
					+ cTrans.getAuthId()
					+ ">"
					+ "<input type=\"hidden\" name=\"transactionID\" value="
					+ cTrans.getTransactionId() + ">";
			message += "<input type=\"submit\" value=\"Approve\"></form>";

			message += "<form autocomplete=\"off\"  name=form2 method=\"POST\" action=\"CM_EscalationForm.do\">"
					+ "<input type=\"hidden\" name=\"AuthID\" value="
					+ cTrans.getAuthId()
					+ ">"
					+ "<input type=\"hidden\" name=\"transactionID\" value="
					+ cTrans.getTransactionId() + ">";
			message += "<input type=\"submit\" value=\"Escalate\"></form>" + "";

			currentView.setMessage(message);
			return new ModelAndView("CM_ProcessRequest", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/CM_Approve.do")
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
			CM_Model Mgr = new CM_Model();
			String resultOfDelete = Mgr.deleteCritTransaction(AuthID,
					transactionID);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfDelete);
			return new ModelAndView("CM_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/CM_EscalationForm.do")
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
			message += "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"CM_ProcessEscalate.do\">"

					+ "<input type=\"hidden\" name=\"AuthID\" value="
					+ AuthID
					+ ">"
					+ "<input type=\"hidden\" name=\"transactionID\" value="
					+ transactionID + ">";
			message += "<select name = \"CorpManager\" >";
			for (String currentdmID : corpMgrList.keySet()) {
				if (!currentdmID.equals(UserIDLoggedIn)) {
					message += "<option value = \'" + currentdmID + "\'>"
							+ corpMgrList.get(currentdmID) + " (" + currentdmID
							+ ")" + "</option>";
				}
			}
			message += "</select><input type=\"submit\" value=\"Escalate\"></form>"
					+ "";
			currentView.setMessage(message);
			return new ModelAndView("CM_EscalationForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/CM_ProcessEscalate.do")
	public ModelAndView EscalateTransaction(@RequestParam int AuthID,
			@RequestParam String CorpManager) {
		try {
			if (Login_Model.isInputBad(CorpManager)) {
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
			CM_Model cMgr = new CM_Model();
			String resultOfDelete = cMgr.addCriticalTransaction(AuthID,
					CorpManager);
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			currentView.setMessage(resultOfDelete);
			return new ModelAndView("CM_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

}
