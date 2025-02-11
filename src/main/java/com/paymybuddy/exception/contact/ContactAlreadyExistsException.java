package com.paymybuddy.exception.contact;

import com.paymybuddy.exception.CustomException;

public class ContactAlreadyExistsException extends CustomException {
    public ContactAlreadyExistsException(int userId) {
        super("Contact already exists with id : " + String.valueOf(userId));
    }

    public ContactAlreadyExistsException() {
        super("Contact already exists");
    }
}
