package org.saga.example.payment.entity;

public class LoginMessage {

	String message;

	Boolean status;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public LoginMessage(String message, Boolean status) {
		super();
		this.message = message;
		this.status = status;
	}

	public LoginMessage() {
		super();
	}

}
