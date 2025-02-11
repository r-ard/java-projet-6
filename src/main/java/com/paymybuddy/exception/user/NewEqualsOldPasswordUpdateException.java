package com.paymybuddy.exception.user;

import com.paymybuddy.exception.CustomException;

public class NewEqualsOldPasswordUpdateException extends CustomException {
    public NewEqualsOldPasswordUpdateException() { super("New password and old password is the same"); }
}
