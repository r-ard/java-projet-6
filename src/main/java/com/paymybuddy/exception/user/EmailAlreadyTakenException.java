package com.paymybuddy.exception.user;

import com.paymybuddy.exception.CustomException;

public class EmailAlreadyTakenException extends CustomException {
    public EmailAlreadyTakenException() {
        super("Email address is already taken");
    }

    public EmailAlreadyTakenException(String email) {
        super("Email address '" + email + "' is already taken");
    }
}
