package mypkg003.simple.securitypattern;

public class Client extends Facade {
	void authenticate(HttpServletRequest request) {
		String username = request.getParameter("user");
		String account = request.getParameter("account");
		String contact = request.getParameter("contact");

		if (InputValidator.validate() && !getUserInfo().equals(username)) {
			// WARN
			return;
		}
		if (InputValidator.validate() && !getAccountInfo().equals(account)) {
			// WARN
			return;
		}
		if (InputValidator.validate() && !getContacts().equals(contact)) {
			updateAccountInfo();
		}
	}
}

class Facade {
	Login login;
	AccountManager accountManager;
	ContactForm contactForm;

	Object getUserInfo() {
		return login.getUserInfo();
	}

	void setUserInfo(Object o) {
		login.setUserInfo(o);
	}

	Object getAccountInfo() {
		return accountManager.getAccountInfo();
	}

	void updateAccountInfo() {
		accountManager.updateAccountInfo();
	}

	void setAccountInfo() {
		accountManager.setAccountInfo();
	}

	Object getContacts() {
		return contactForm.getContacts();
	}

	void setContacts(Object o) {
		contactForm.setContacts(o);
	}
}

class Login {
	Object getUserInfo() {
		return null;
	}

	void setUserInfo(Object o) {
	}
}

class AccountManager {
	Object getAccountInfo() {
		return null;
	}

	void setAccountInfo() {
	}

	void updateAccountInfo() {
	}
}

class ContactForm {
	Object getContacts() {
		return null;
	}

	void setContacts(Object o) {
	}
}