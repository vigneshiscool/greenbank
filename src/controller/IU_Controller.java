package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import model.IU_Model;
import model.Login_Model;

import org.hibernate.HibernateException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import view.Login_View;
import entity.ExternalUsers;
import entity.Transactions;
import exception.ApplicationException;

@Controller
public class IU_Controller {
	@RequestMapping(value = "/IU_NewDepositForm.do")
	public ModelAndView showNewDepositForm() {
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
			Date timeNow = new Date();
			IU_Model IUModel = new IU_Model();
			HashMap<String, String> regularEmployees = IUModel
					.getListOfRegularEmployees();
			if (regularEmployees == null) {
				return new ModelAndView("BadInput");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String currentTime = sdf.format(timeNow);

			String message = "";
			message += "<h2>New Deposit</h2> <br><br>"
					+ "Transaction time - "
					+ currentTime
					+ "<br>"
					+ "Deposit to - "
					+ UserIDLoggedIn
					+ "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"IU_CreateNewTransaction.do\">"

					+ "<input type=\"hidden\" name=\"transaction_from\" value="
					+ UserIDLoggedIn
					+ "><input type=\"hidden\" name=\"transaction_time\" value="
					+ currentTime
					+ "><br> Amount <input type=text name=transaction_amount /><br><br> "
					+ "<input type = hidden name = transaction_description value = \"Cash deposit\">"
					+ "<input type = hidden name = transaction_to value = \"cashdeposit\">";
			message += "<br> Employee working with <select name = employee_name>";
			for (String currentUserId : regularEmployees.keySet()) {
				message += "<option value = \"" + currentUserId + "\">"
						+ regularEmployees.get(currentUserId) + "("
						+ currentUserId + ")</option>";
			}
			message += "</select>";
			message += "<input type = hidden name = transaction_type value = \"1\">"
					+ "<br><input type = submit value = \"Deposit\"></form>"
					+ "";

			currentView.setMessage(message);
			return new ModelAndView("IU_NewTransactionForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/IU_CreateNewTransaction.do")
	public ModelAndView createNewTransactionAndAuthorization(
			@RequestParam String transaction_from,
			@RequestParam String transaction_time,
			@RequestParam String transaction_amount,
			@RequestParam String transaction_description,
			@RequestParam String transaction_to,
			@RequestParam int transaction_type,
			@RequestParam String employee_name) {
		try {

			if (Login_Model.isInputBad(transaction_from)
					|| Login_Model.isInputBad(transaction_to)
					|| Login_Model.isInputBad(employee_name)
					|| Login_Model.isInputBad(transaction_time)
					|| Login_Model.isInputBad(transaction_description)) {
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

			if (transaction_amount.trim().equals("")) {

				Login_View v2 = new Login_View();
				v2.setUserID(UserIDLoggedIn);
				v2.setMessage("You should enter amount.");
				return new ModelAndView("IU_Home", "LoginView", v2);

			}
			Double actualTransactionAmount = 0.0;
			try {
				actualTransactionAmount = Double
						.parseDouble(transaction_amount);
			} catch (Exception e) {
				Login_View v2 = new Login_View();
				v2.setUserID(UserIDLoggedIn);
				v2.setMessage("Invalid amount.");
				return new ModelAndView("IU_Home", "LoginView", v2);
			}
			if (actualTransactionAmount < (Double) 0.0) {

				Login_View v2 = new Login_View();
				v2.setUserID(UserIDLoggedIn);
				v2.setMessage("Invalid amount.");
				return new ModelAndView("IU_Home", "LoginView", v2);

			}
			Login_View currentView = new Login_View();
			currentView.setUserID(UserIDLoggedIn);
			IU_Model IUModel = new IU_Model();
			String result = IUModel.createNewNormalTransaction(UserIDLoggedIn,
					transaction_from, transaction_time,
					actualTransactionAmount, transaction_description,
					transaction_to, transaction_type, employee_name);
			currentView.setMessage(result);
			return new ModelAndView("IU_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/IU_NewWithdrawForm.do")
	public ModelAndView showNewWithdrawForm() {
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
			Date timeNow = new Date();
			IU_Model IUModel = new IU_Model();
			HashMap<String, String> regularEmployees = IUModel
					.getListOfRegularEmployees();
			if (regularEmployees == null) {
				return new ModelAndView("BadInput");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String currentTime = sdf.format(timeNow);

			String message = "";
			message += "<h2>New Withdraw</h2> <br><br>"
					+ " Transaction time - "
					+ currentTime
					+ "<br>"
					+ "Withdraw from - "
					+ UserIDLoggedIn
					+ "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"IU_CreateNewTransaction.do\">"

					+ "<input type=\"hidden\" name=\"transaction_from\" value="
					+ UserIDLoggedIn
					+ "><input type=\"hidden\" name=\"transaction_time\" value="
					+ currentTime
					+ "><br> Amount <input type=text name=transaction_amount /><br><br> "
					+ "<input type = hidden name = transaction_description value = \"Cash Withdrawal\">"
					+ "<input type = hidden name = transaction_to value = \"cashwithdraw\">";
			message += "<br> Employee working with <select name = employee_name>";
			for (String currentUserId : regularEmployees.keySet()) {
				message += "<option value = \"" + currentUserId + "\">"
						+ regularEmployees.get(currentUserId) + "("
						+ currentUserId + ")</option>";
			}
			message += "</select>";
			message += "<br><input type = hidden name = transaction_type value = \"2\">"
					+ "<input type = submit value = \"Withdraw\"></form>" + "";

			currentView.setMessage(message);
			return new ModelAndView("IU_NewTransactionForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/IU_NewTransferForm.do")
	public ModelAndView showNewMoneyTransferForm() {
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
			Date timeNow = new Date();
			IU_Model IUModel = new IU_Model();
			HashMap<String, String> regularEmployees = IUModel
					.getListOfRegularEmployees();
			if (regularEmployees == null) {
				return new ModelAndView("BadInput");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String currentTime = sdf.format(timeNow);

			String message = "";
			message += "<h2>New Money Transfer</h2> <br><br>"
					+ " Transaction time - "
					+ currentTime
					+ "<br>"
					+ "Transfer from - "
					+ UserIDLoggedIn
					+ "<form autocomplete=\"off\"  name=form1 method=\"POST\" action=\"IU_CreateNewTransaction.do\">"

					+ "<input type=\"hidden\" name=\"transaction_from\" value="
					+ UserIDLoggedIn
					+ "><input type=\"hidden\" name=\"transaction_time\" value="
					+ currentTime
					+ "><br> Transfer to <input type = text name = transaction_to >"
					+ "<br> Amount <input type=text name=transaction_amount /> "
					+ "<br> Description <input type = text name = transaction_description>"
					+ "(Brief note about the transaction)";
			message += "<br> Employee working with <select name = employee_name>";
			for (String currentUserId : regularEmployees.keySet()) {
				message += "<option value = \"" + currentUserId + "\">"
						+ regularEmployees.get(currentUserId) + "("
						+ currentUserId + ")</option>";
			}
			message += "</select>";
			message += "<input type = hidden name = transaction_type value = \"3\">"
					+ "<br><br><input type = submit value = \"Transfer\"></form>"
					+ "";

			currentView.setMessage(message);
			return new ModelAndView("IU_NewTransactionForm", "LoginView",
					currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/IU_ViewAccountDetails.do")
	public ModelAndView viewUserAccountDetails() {
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
			Date timeNow = new Date();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String currentTime = sdf.format(timeNow);

			String message = "";
			message += "<h2>Account Details</h2> <br><br>" + " Time - "
					+ currentTime + "<br>";
			IU_Model IUModel = new IU_Model();
			ExternalUsers currentUserAccount = IUModel
					.getAccountDetails(UserIDLoggedIn);
			if (currentUserAccount == null) {
				return new ModelAndView("BadInput");
			}
			message += "Name - " + currentUserAccount.getUserName() + " <br>";
			message += "Account type - ";
			if (currentUserAccount.getUser_type() == 1) {
				message += " Individual User Account <br>";
			} else {
				message += "Merchant Account <br>";
			}
			message += "Account Balance - "
					+ currentUserAccount.getBalance_amount() + " <br>";
			message += "EMail-ID - " + currentUserAccount.getEmail_id()
					+ " <br>";

			currentView.setMessage(message);
			return new ModelAndView("IU_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/IU_ViewTransactionDetails.do")
	public ModelAndView viewUserTransactionDetails() {
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
			Date timeNow = new Date();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String currentTime = sdf.format(timeNow);

			String message = "";
			message += "<h2>Transactions Log</h2> <br><br>" + " Time - "
					+ currentTime + "<br>";

			IU_Model IUModel = new IU_Model();
			List currentUserTransactions = IUModel
					.getTransactions(UserIDLoggedIn);
			if (currentUserTransactions == null) {
				return new ModelAndView("BadInput");
			}
			if (currentUserTransactions.size() == 0) {
				message += "<br><br> No transactions made. <br><br>";
			} else {
				message += "<table border = 1><tr>" + "<th>Date / Time</th>"
						+ "<th>Amount</th>" + "<th>Sender</th>"
						+ "<th>Receiver</th>" + "<th>Description</th>"
						+ "<th>Status</th>" + "</tr>";
				for (Object currentTransactionObject : currentUserTransactions) {
					Transactions currentTransaction = (Transactions) currentTransactionObject;
					double transactionAmount = currentTransaction
							.getTransactionAmount();
					if (transactionAmount < (double) 0) {
						transactionAmount *= -1;
					}
					message += "<tr>" + "<td>"
							+ currentTransaction.getTransactionDatetime()
							+ "</td>" + "<td>" + transactionAmount + "</td>"
							+ "<td>" + currentTransaction.getTransactionFrom()
							+ "</td>" + "<td>"
							+ currentTransaction.getTransactionTo() + "</td>"
							+ "<td>"
							+ currentTransaction.getTransactionDescription()
							+ "</td>" + "<td>"
							+ currentTransaction.getTransactionStatus()
							+ "</td>" + "</tr>";
				}
				message += "</table>";
			}
			currentView.setMessage(message);
			return new ModelAndView("IU_Home", "LoginView", currentView);
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}
}
