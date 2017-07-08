package com.challenge.transactions;

import com.challenge.transactions.model.OutOfDateTransactionException;
import com.challenge.transactions.model.Statistics;
import com.challenge.transactions.model.TimedStatistics;
import com.challenge.transactions.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.List;

import static java.lang.String.format;
import static java.time.Instant.now;

@Service
public class TransactionStatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionStatisticsService.class);
    private final DataStorage dataStorage;
    private final long interval;

    @Autowired
    public TransactionStatisticsService(@Value("${interval.milliseconds}") long interval,
                                        DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.interval = interval;
    }

    private boolean isWithinLastMinute(long current, Transaction transaction) {
        long delta = (current - transaction.getEpochMilliseconds());
        return delta < interval && delta >= 0;
    }

    void persist(Transaction transaction) {
        long currentMillis = now().toEpochMilli();
        if (isWithinLastMinute(currentMillis, transaction)) {
            DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();
            statistics.accept(transaction.getAmount());
            dataStorage.persist(currentMillis, new TimedStatistics(transaction.getEpochMilliseconds(), statistics));
            logger.info("persisted transaction {}-{} at {}", transaction.getAmount(), transaction.getEpochMilliseconds(), currentMillis);
        } else {
            throw new OutOfDateTransactionException(format("Outdated transaction, %s at %s", transaction, now().toEpochMilli()));
        }
    }

    Statistics aggregate() {
        List<DoubleSummaryStatistics> statsSummary = dataStorage.findStatisticsUpTo(now().toEpochMilli());
        logger.info("Aggregating {} for the past minute", statsSummary.size());
        return Statistics.of(statsSummary
                .stream().reduce(this::combine).orElse(new DoubleSummaryStatistics()));
    }

    private DoubleSummaryStatistics combine(DoubleSummaryStatistics first, DoubleSummaryStatistics second) {
        first.combine(second);
        return first;
    }
}
