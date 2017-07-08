package com.challenge.transactions;

import com.challenge.transactions.model.TimedStatistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Component
class DataStorage {

    private final TimedStatistics[] store;

    DataStorage(@Value("${interval.milliseconds}") int interval) {
        this.store = new TimedStatistics[interval];
    }

    synchronized void persist(long currentInstant, TimedStatistics toPersist) {
        int index = getIndex(currentInstant - toPersist.getTimestamp());
        TimedStatistics previous = ofNullable(store[index]).orElse(new TimedStatistics());
        if (previous.getTimestamp() == toPersist.getTimestamp()) {
            DoubleSummaryStatistics combined = previous.getStatistics();
            combined.combine(toPersist.getStatistics());
            store[index] = new TimedStatistics(previous.getTimestamp(), combined);
        } else {
            store[index] = toPersist;
        }
    }

    synchronized List<DoubleSummaryStatistics> findStatisticsUpTo(long instant) {
        return IntStream.range(0, store.length)
                .filter(i -> ofNullable(store[i]).isPresent())
                .filter(i -> (instant - store[i].getTimestamp()) < store.length)
                .mapToObj(i -> store[i].getStatistics())
                .collect(toList());
    }

    private int getIndex(long delta) {
        return (int) (delta) % store.length;
    }
}
