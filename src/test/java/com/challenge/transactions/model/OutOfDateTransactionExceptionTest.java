package com.challenge.transactions.model;


import org.junit.Test;

public class OutOfDateTransactionExceptionTest {

    @Test
    public void canBeInstantiated() {
        new OutOfDateTransactionException("test");
    }
}