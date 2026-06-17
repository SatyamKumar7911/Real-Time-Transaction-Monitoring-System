package com.example.accountservice.exception;
public class InsufficientFundsException extends RuntimeException {
public InsufficientFundsException(String msg){ 
	super(msg);
	}
}
