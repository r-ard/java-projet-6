package com.paymybuddy.exception.user;

import com.paymybuddy.exception.CustomException;

public class UsernameAlreadyTakenException extends CustomException {
    public UsernameAlreadyTakenException() {
        super("Username is already taken");
    }

    public UsernameAlreadyTakenException(String username) {
        super("Username '" + username + "' is already taken");
    }
}
