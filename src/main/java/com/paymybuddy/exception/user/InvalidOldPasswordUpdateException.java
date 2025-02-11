package com.paymybuddy.exception.user;

import com.paymybuddy.exception.CustomException;

public class InvalidOldPasswordUpdateException extends CustomException {
    public InvalidOldPasswordUpdateException() {
        super("Invalid old password");
    }
}
