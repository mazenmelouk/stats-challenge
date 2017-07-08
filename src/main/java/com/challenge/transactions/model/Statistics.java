package com.challenge.transactions.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.DoubleSummaryStatistics;
import java.util.Objects;

public final class Statistics {

    private final double sum;
    private final double average;
    private final double min;
    private final double max;
    private final long count;

    @JsonCreator
    private Statistics(@JsonProperty("sum") double sum,
                       @JsonProperty("average") double average,
                       @JsonProperty("min") double min,
                       @JsonProperty("max") double max,
                       @JsonProperty("count") long count) {
        this.sum = sum;
        this.average = average;
        this.min = min;
        this.max = max;
        this.count = count;
    }

    public static Statistics of(DoubleSummaryStatistics summaryStatistics) {
        return new Statistics(summaryStatistics.getSum(),
                summaryStatistics.getAverage(), summaryStatistics.getMin(), summaryStatistics.getMax(), summaryStatistics.getCount());
    }

    public double getSum() {
        return sum;
    }

    public double getAverage() {
        return average;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public long getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Statistics that = (Statistics) o;

        return Objects.equals(count, that.count) && Objects.equals(min, that.min)
                && Objects.equals(max, that.max) && Objects.equals(average, that.average)
                && Objects.equals(sum, that.sum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(max, count, min, average, sum);
    }
}
