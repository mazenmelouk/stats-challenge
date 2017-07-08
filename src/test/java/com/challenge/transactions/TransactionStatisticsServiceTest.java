package com.challenge.transactions;

import com.challenge.transactions.model.OutOfDateTransactionException;
import com.challenge.transactions.model.Statistics;
import com.challenge.transactions.model.TimedStatistics;
import com.challenge.transactions.model.Transaction;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionStatisticsServiceTest {
    private static final double AMOUNT = 12.0;
    private static final long NOW = Instant.now().toEpochMilli();
    private static final long INTERVAL = 60000;
    private static final DoubleSummaryStatistics EMPTY_STATISTICS = new DoubleSummaryStatistics();


    @Mock
    private DataStorage dataStorage;

    private TransactionStatisticsService transactionStatisticsService;

    @Before
    public void setup() {
        transactionStatisticsService = new TransactionStatisticsService(INTERVAL, dataStorage);

    }

    @Test
    public void testValidPersist() {
        Transaction transaction = new Transaction(AMOUNT, NOW);
        transactionStatisticsService.persist(transaction);
        ArgumentCaptor<TimedStatistics> captor = ArgumentCaptor.forClass(TimedStatistics.class);

        verify(dataStorage).persist(anyLong(), captor.capture());

        TimedStatistics argument = captor.getValue();
        TimedStatistics expected = constructFromTransaction(transaction);

        assertEquals(argument.getStatistics().getAverage(), expected.getStatistics().getAverage(), 0);
        assertEquals(argument.getStatistics().getSum(), expected.getStatistics().getSum(), 0);
        assertEquals(argument.getStatistics().getMax(), expected.getStatistics().getMax(), 0);
        assertEquals(argument.getStatistics().getMin(), expected.getStatistics().getMin(), 0);
        assertEquals(argument.getStatistics().getCount(), expected.getStatistics().getCount());
        assertEquals(argument.getTimestamp(), expected.getTimestamp());

    }

    @Test(expected = OutOfDateTransactionException.class)
    public void testInvalidOldPersist() {
        Transaction transaction = new Transaction(AMOUNT, NOW - INTERVAL);
        transactionStatisticsService.persist(transaction);
    }

    @Test(expected = OutOfDateTransactionException.class)
    public void testInvalidFuturePersist() {
        Transaction transaction = new Transaction(AMOUNT, NOW + INTERVAL);
        transactionStatisticsService.persist(transaction);
    }

    @Test
    public void testAggregateNoData() {
        when(dataStorage.findStatisticsUpTo(anyLong())).thenReturn(emptyList());
        Statistics statistics = transactionStatisticsService.aggregate();

        assertEquals(statistics.getAverage(), EMPTY_STATISTICS.getAverage(), 0);
        assertEquals(statistics.getSum(), EMPTY_STATISTICS.getSum(), 0);
        assertEquals(statistics.getMax(), EMPTY_STATISTICS.getMax(), 0);
        assertEquals(statistics.getMin(), EMPTY_STATISTICS.getMin(), 0);
        assertEquals(statistics.getCount(), EMPTY_STATISTICS.getCount());
    }

    @Test
    public void testAggregate() {
        DoubleSummaryStatistics statistics1 = new DoubleSummaryStatistics();
        statistics1.accept(10);
        DoubleSummaryStatistics statistics2 = new DoubleSummaryStatistics();
        statistics1.accept(20);
        when(dataStorage.findStatisticsUpTo(anyLong())).thenReturn(ImmutableList.of(statistics1, statistics2));

        Statistics statistics = transactionStatisticsService.aggregate();

        assertEquals(statistics.getAverage(), 15, 0);
        assertEquals(statistics.getSum(), 30, 0);
        assertEquals(statistics.getMax(), 20, 0);
        assertEquals(statistics.getMin(), 10, 0);
        assertEquals(statistics.getCount(), 2);

    }


    private TimedStatistics constructFromTransaction(Transaction transaction) {
        DoubleSummaryStatistics summaryStatistics = new DoubleSummaryStatistics();
        summaryStatistics.accept(transaction.getAmount());
        return new TimedStatistics(transaction.getEpochMilliseconds(), summaryStatistics);
    }

}