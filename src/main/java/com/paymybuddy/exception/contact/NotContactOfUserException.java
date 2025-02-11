package com.paymybuddy.exception.contact;

import com.paymybuddy.exception.CustomException;

public class NotContactOfUserException extends CustomException {
    public NotContactOfUserException(int contactId) {
        super("Not contact of user : " + contactId);
    }

    public NotContactOfUserException() {
        super("Not contact of user");
    }
}
