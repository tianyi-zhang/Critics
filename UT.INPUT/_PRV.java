package mypkg003.simple.securitypattern;

public class Client {
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

	void setAccountInfo() {
		accountManager.setAccountInfo();
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

	void setContacts() {
	}
}
