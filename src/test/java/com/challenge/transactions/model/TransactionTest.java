package com.challenge.transactions.model;


import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.io.IOException;

import static com.challenge.transactions.TestUtils.mapper;
import static org.junit.Assert.assertEquals;

public class TransactionTest {

    private static final double AMOUNT = 12.3;
    private static final long TIMESTAMP = System.currentTimeMillis();

    @Test
    public void testGetters() {
        Transaction test = new Transaction(AMOUNT, TIMESTAMP);
        assertEquals(test.getAmount(), AMOUNT, 0);
        assertEquals(test.getEpochMilliseconds(), TIMESTAMP);
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(Transaction.class).verify();
    }

    @Test
    public void testJsonDeserialization() throws IOException {
        String json = "{\"amount\":" + AMOUNT + ",\"timestamp\":" + TIMESTAMP + "}";
        Transaction test = mapper().readValue(json, Transaction.class);
        Transaction expected = new Transaction(AMOUNT, TIMESTAMP);

        assertEquals(expected, test);
    }

    @Test
    public void testToString() {
        String test = new Transaction(AMOUNT, TIMESTAMP).toString();
        String expected = "Transaction{amount=" + AMOUNT + ", epochMilliseconds=" + TIMESTAMP + "}";

        assertEquals(expected, test);
    }
}