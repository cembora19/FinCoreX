package com.fincorex.exception;

public class InsufficientAssetException extends BusinessException {

    public InsufficientAssetException() {
        super("Insufficient asset quantity");
    }
}
