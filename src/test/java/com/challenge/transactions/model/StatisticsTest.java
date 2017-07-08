package com.challenge.transactions.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.DoubleSummaryStatistics;

import static com.challenge.transactions.TestUtils.mapper;
import static org.junit.Assert.assertEquals;

public class StatisticsTest {

    private static final double AMOUNT = 42;
    private DoubleSummaryStatistics doubleSummaryStatistics;

    @Before
    public void setup() {
        doubleSummaryStatistics = new DoubleSummaryStatistics();
        doubleSummaryStatistics.accept(AMOUNT);
    }

    @Test
    public void testConstructFromDoubleSummaryStats() {
        Statistics statistics = Statistics.of(doubleSummaryStatistics);

        assertEquals(statistics.getAverage(), doubleSummaryStatistics.getAverage(), 0);
        assertEquals(statistics.getCount(), doubleSummaryStatistics.getCount(), 0);
        assertEquals(statistics.getMax(), doubleSummaryStatistics.getMax(), 0);
        assertEquals(statistics.getMin(), doubleSummaryStatistics.getMin(), 0);
        assertEquals(statistics.getSum(), doubleSummaryStatistics.getSum(), 0);
    }

    @Test
    public void shouldSerializeToJson() throws JsonProcessingException, JSONException {
        Statistics statistics = Statistics.of(doubleSummaryStatistics);
        String json = mapper().writeValueAsString(statistics);
        String expected = "{\"sum\":" + AMOUNT + ",\"average\":" + AMOUNT + ",\"min\":" + AMOUNT + ",\"max\":" + AMOUNT + ",\"count\":1}";

        JSONAssert.assertEquals(json, expected, true);
    }


    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(Statistics.class).verify();
    }

}