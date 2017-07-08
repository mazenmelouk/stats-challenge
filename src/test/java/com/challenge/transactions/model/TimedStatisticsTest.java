package com.challenge.transactions.model;


import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.DoubleSummaryStatistics;

import static org.junit.Assert.assertEquals;

public class TimedStatisticsTest {

    @Test
    public void hasDefaultConstructor() {
        new TimedStatistics();
    }

    @Test
    public void canBeConstructedWithParams() {
        long timestamp = 2111;
        DoubleSummaryStatistics doubleSummaryStatistics = new DoubleSummaryStatistics();
        TimedStatistics statistics = new TimedStatistics(timestamp, doubleSummaryStatistics);

        assertEquals(doubleSummaryStatistics, statistics.getStatistics());
        assertEquals(timestamp, statistics.getTimestamp());
    }

    @Test(expected = NullPointerException.class)
    public void statisticsCannotBeNull() {
        new TimedStatistics(2113, null);
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(TimedStatistics.class).verify();
    }

}