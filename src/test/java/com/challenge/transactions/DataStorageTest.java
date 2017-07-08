package com.challenge.transactions;

import com.challenge.transactions.model.TimedStatistics;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class DataStorageTest {

    private static final long REFERENCE_POINT = Instant.now().toEpochMilli();
    private static final long EARLIER_10_MS = REFERENCE_POINT - 10;
    private static final long EARLIER_20_MS = REFERENCE_POINT - 20;
    private static final int INTERVAL = 60000;

    private static final DoubleSummaryStatistics STATISTICS_1 = new DoubleSummaryStatistics();
    private static final DoubleSummaryStatistics STATISTICS_2 = new DoubleSummaryStatistics();


    private DataStorage store;

    @Before
    public void setup() {
        STATISTICS_1.accept(12.5);
        STATISTICS_2.accept(7.5);
        store = new DataStorage(INTERVAL);
    }


    @Test
    public void testPersistOne() {
        store.persist(REFERENCE_POINT, new TimedStatistics(EARLIER_10_MS, STATISTICS_1));

        List<DoubleSummaryStatistics> aggregated = store.findStatisticsUpTo(REFERENCE_POINT);

        assertThat(aggregated, hasItems(STATISTICS_1));
    }

    @Test
    public void testPersistMultipleDifferentTimestamps() {
        store.persist(REFERENCE_POINT, new TimedStatistics(EARLIER_10_MS, STATISTICS_1));
        store.persist(REFERENCE_POINT, new TimedStatistics(EARLIER_20_MS, STATISTICS_2));

        List<DoubleSummaryStatistics> aggregated = store.findStatisticsUpTo(REFERENCE_POINT);

        assertThat(aggregated, hasItems(STATISTICS_1, STATISTICS_2));
    }

    @Test
    public void testPersistMultipleSameTimestamps() {
        store.persist(REFERENCE_POINT, new TimedStatistics(EARLIER_10_MS, STATISTICS_1));
        store.persist(REFERENCE_POINT, new TimedStatistics(EARLIER_10_MS, STATISTICS_2));

        List<DoubleSummaryStatistics> aggregated = store.findStatisticsUpTo(REFERENCE_POINT);
        DoubleSummaryStatistics combined = STATISTICS_1;
        combined.combine(STATISTICS_2);

        assertThat(aggregated, hasItems(combined));
    }

    @Test
    public void testPersistReplacingOlder() {
        store.persist(REFERENCE_POINT, new TimedStatistics(EARLIER_10_MS, STATISTICS_1));
        store.persist(REFERENCE_POINT + INTERVAL, new TimedStatistics(EARLIER_10_MS + INTERVAL, STATISTICS_2));

        List<DoubleSummaryStatistics> aggregated = store.findStatisticsUpTo(REFERENCE_POINT + INTERVAL);

        assertThat(aggregated, hasItems(STATISTICS_2));
    }

    @Test
    public void testPersistIgnoresOlder() {
        store.persist(REFERENCE_POINT, new TimedStatistics(EARLIER_10_MS, STATISTICS_1));
        store.persist(REFERENCE_POINT + INTERVAL, new TimedStatistics(EARLIER_20_MS + INTERVAL, STATISTICS_2));

        List<DoubleSummaryStatistics> aggregated = store.findStatisticsUpTo(REFERENCE_POINT + INTERVAL);

        assertThat(aggregated, hasItems(STATISTICS_2));
    }


}