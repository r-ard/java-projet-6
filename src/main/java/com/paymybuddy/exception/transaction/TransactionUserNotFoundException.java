package com.paymybuddy.exception.transaction;

import com.paymybuddy.exception.CustomException;

public class TransactionUserNotFoundException extends CustomException {
    public TransactionUserNotFoundException() {
        super("Transaction user not found");
    }
}
