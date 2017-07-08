package com.challenge.transactions.model;


public class OutOfDateTransactionException extends RuntimeException {
    public OutOfDateTransactionException(String message) {
        super(message);
    }
}
