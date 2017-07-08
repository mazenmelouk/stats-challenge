package com.challenge.transactions.model;


import java.util.DoubleSummaryStatistics;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class TimedStatistics {

    private final long timestamp;
    private final DoubleSummaryStatistics statistics;

    public TimedStatistics(long timestamp, DoubleSummaryStatistics statistics) {
        this.timestamp = timestamp;
        this.statistics = requireNonNull(statistics);
    }

    public TimedStatistics() {
        this(-1, new DoubleSummaryStatistics());
    }

    public long getTimestamp() {
        return timestamp;
    }

    public DoubleSummaryStatistics getStatistics() {
        return statistics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimedStatistics that = (TimedStatistics) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(statistics, that.statistics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, statistics);
    }
}
