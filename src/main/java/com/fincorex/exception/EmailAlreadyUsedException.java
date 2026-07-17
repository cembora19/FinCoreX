package com.fincorex.exception;

public class EmailAlreadyUsedException extends BusinessException {

    public EmailAlreadyUsedException(String email) {
        super("Email is already in use: " + email);
    }
}
