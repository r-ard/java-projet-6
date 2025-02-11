package com.paymybuddy.exception;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException(int userId) {
        super("Can not find user with id : " + String.valueOf(userId));
    }

    public UserNotFoundException() {
        super("Can not find user");
    }
}
