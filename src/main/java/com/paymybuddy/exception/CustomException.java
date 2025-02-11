package com.paymybuddy.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CustomException extends Exception {
    public CustomException(String exception) {
        super("CustomException : " + exception);
        this.getLogger().error(exception);
    }

    public CustomException() {
        super("CustomException");
        this.getLogger().error("an error has been thrown");
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
