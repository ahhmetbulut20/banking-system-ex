package com.ahhmet.banking.model;

import org.springframework.stereotype.Component;

@Component
public class AccountModel {
	
	private String name;
	private String surname;
	private String email;
	private String tc;
	private String type;
	private int accountNumber;
	private double balance;
	
	public AccountModel() {
		balance=0;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTc() {
		return tc;
	}
	public void setTc(String tc) {
		this.tc = tc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(int i) {
		this.accountNumber = i;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	
}
