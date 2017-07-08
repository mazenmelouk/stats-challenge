package com.challenge.transactions.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class Transaction {
    private final double amount;
    private final long epochMilliseconds;

    @JsonCreator
    public Transaction(@JsonProperty("amount") double amount, @JsonProperty("timestamp") long epochMilliseconds) {
        this.amount = amount;
        this.epochMilliseconds = epochMilliseconds;
    }

    public double getAmount() {
        return amount;
    }

    public long getEpochMilliseconds() {
        return epochMilliseconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        return Objects.equals(amount, that.amount)
                && Objects.equals(epochMilliseconds, that.epochMilliseconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, epochMilliseconds);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", epochMilliseconds=" + epochMilliseconds +
                '}';
    }
}
