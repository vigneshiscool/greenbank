package controller;

import org.apache.commons.codec.digest.*;

import model.Login_Model;

import org.hibernate.HibernateException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import view.Login_View;
import entity.ExternalUsers;
import entity.InternalUsers;
import exception.ApplicationException;

@Controller
public class Login_Controller {

	@RequestMapping(value = "/403.do", method = RequestMethod.GET)
	public String accessDenied(ModelMap model) {

		return "403";

	}

	@RequestMapping(value = "/Userlogin.do", method = RequestMethod.GET)
	public String login(ModelMap model) {

		return "Userlogin";

	}

	@RequestMapping(value = "/loginfailed.do", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {

		model.addAttribute("error", "true");
		return "Userlogin";

	}

	@RequestMapping(value = "/logout.do", method = RequestMethod.GET)
	public String logout(ModelMap model) {

		return "Userlogin";

	}

	@RequestMapping(value = "/LoginType.do", method = RequestMethod.GET)
	public ModelAndView LoginType_Home() throws HibernateException,
			ApplicationException {
		String UserID = "";
		try {
			User user = (User) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
			UserID = user.getUsername();
		} catch (ClassCastException ex) {
			return new ModelAndView("403");
		}
		try {
			if (UserID != null) {
				Login_Model login = new Login_Model();
				{
					if (login.isOTPSet(UserID)) {

						if (login.isInternal(UserID)) {
							InternalUsers currentUser = login
									.getInternalUser(UserID);
							boolean OTPSet = login.isOTPSet(UserID);
							String message = "Welcome "
									+ currentUser.getUserName() + "!";
							Login_View view = new Login_View();
							view.setMessage(message);

							view.setUserID(UserID);

							if (currentUser.getUserRole() == 1) {
								return new ModelAndView("CM_Home", "LoginView",
										view);
							} else if (currentUser.getUserRole() == 2) {
								return new ModelAndView("DM_Home", "LoginView",
										view);
							} else if (currentUser.getUserRole() == 6) {
								return new ModelAndView("SA_Home", "LoginView",
										view);
							} else if (currentUser.getUserRole() == 5) {
								return new ModelAndView("RE_Home", "LoginView",
										view);
							} else if (currentUser.getUserRole() == 4) {
								return new ModelAndView("SAL_Home",
										"LoginView", view);
							} else {
								return new ModelAndView("OTHER_Home",
										"LoginView", view);
							}
						} else if (login.isExternal(UserID)) {
							ExternalUsers currentUser = login
									.getExternalUser(UserID);
							String message = "Welcome "
									+ currentUser.getUserName() + "!";
							Login_View view = new Login_View();
							view.setMessage(message);

							view.setUserID(UserID);

							if (currentUser.getUser_type() == 1) {
								return new ModelAndView("IU_Home", "LoginView",
										view);
							} else {
								return new ModelAndView("MU_Home", "LoginView",
										view);
							}
						} else {
							String message = "Invalid Login Credentails";
							return new ModelAndView("login", "message", message);
						}
					} else {
						Login_View view = new Login_View();
						view.setUserID(UserID);
						view.setMessage("You should set your passowrd.");
						return new ModelAndView("SetOTP", "LoginView", view);
					}
				}
			} else {
				String message = "Enter all mandatory fields";
				return new ModelAndView("login", "message", message);
			}
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}

	@RequestMapping(value = "/SubmitPassword.do")
	public ModelAndView submitNewPassword(@RequestParam String userID,
			@RequestParam String password, @RequestParam String password1) {
		try {
			String message = "";
			Login_View view = new Login_View();
			view.setUserID(userID);
			if (password.length() < 8) {
				view.setMessage("Password length is insufficient. Password should be atleast 8 charecters long");
				return new ModelAndView("SetOTP", "LoginView", view);
			}
			String password_hash = DigestUtils.sha1Hex(password);
			String password_hash1 = DigestUtils.sha1Hex(password);
			password = password_hash;
			password1 = password_hash1;

			view.setUserID(userID);
			if (!password.equals(password1)) {
				message = "Passwords you entered don't match. Please try again.";
				view.setMessage(message);
				return new ModelAndView("SetOTP", "LoginView", view);
			} else {

				Login_Model LoginModel = new Login_Model();
				boolean otpIsSet = LoginModel.setOTP(userID, password);
				if (otpIsSet == false) {
					view.setMessage("Password reset failed. Please try again");
					return new ModelAndView("SetOTP", "LoginView", view);
				} else {
					if (LoginModel.isInternal(userID)) {
						InternalUsers currentUser = LoginModel
								.getInternalUser(userID);
						boolean OTPSet = LoginModel.isOTPSet(userID);
						message = "Welcome " + currentUser.getUserName() + "!";
						view = new Login_View();
						view.setMessage(message);

						view.setUserID(userID);

						if (currentUser.getUserRole() == 1) {
							return new ModelAndView("CM_Home", "LoginView",
									view);
						} else if (currentUser.getUserRole() == 2) {
							return new ModelAndView("DM_Home", "LoginView",
									view);
						} else if (currentUser.getUserRole() == 6) {
							return new ModelAndView("SA_Home", "LoginView",
									view);
						} else if (currentUser.getUserRole() == 5) {
							return new ModelAndView("RE_Home", "LoginView",
									view);
						} else if (currentUser.getUserRole() == 4) {
							return new ModelAndView("SAL_Home", "LoginView",
									view);
						} else {
							return new ModelAndView("OTHER_Home", "LoginView",
									view);
						}
					} else if (LoginModel.isExternal(userID)) {
						ExternalUsers currentUser = LoginModel
								.getExternalUser(userID);
						message = "Welcome " + currentUser.getUserName() + "!";
						view = new Login_View();
						view.setMessage(message);

						view.setUserID(userID);

						if (currentUser.getUser_type() == 1) {
							return new ModelAndView("IU_Home", "LoginView",
									view);
						} else {
							return new ModelAndView("MU_Home", "LoginView",
									view);
						}
					} else {
						message = "Invalid Login Credentails";
						return new ModelAndView("login", "message", message);
					}
				}

			}
		} catch (Exception e) {
			return new ModelAndView("BadInput");
		}
	}
}
