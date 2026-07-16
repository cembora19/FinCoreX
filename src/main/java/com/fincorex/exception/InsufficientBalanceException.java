package com.fincorex.exception;

public class InsufficientBalanceException extends BusinessException {

    public InsufficientBalanceException() {
        super("Insufficient wallet balance");
    }
}
