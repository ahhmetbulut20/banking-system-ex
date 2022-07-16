package com.ahhmet.banking.message;

import org.springframework.stereotype.Component;

@Component
public class AccountCreateSuccessResponse extends AccountCreateResponse{

	private int accountNumber;
	
	/*private String message;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	*/
	public int getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	
}
