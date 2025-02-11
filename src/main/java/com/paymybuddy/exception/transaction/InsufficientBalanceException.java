package com.paymybuddy.exception.transaction;

import com.paymybuddy.exception.CustomException;

public class InsufficientBalanceException extends CustomException {
    public InsufficientBalanceException(double currentAmount) {
        super("Insufficient balance on user's account, current : " + String.valueOf(currentAmount));
    }

    public InsufficientBalanceException() {
        super("Insufficient balance on user's account");
    }
}
