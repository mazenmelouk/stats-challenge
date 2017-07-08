package com.challenge.transactions;

import com.challenge.transactions.model.OutOfDateTransactionException;
import com.challenge.transactions.model.Statistics;
import com.challenge.transactions.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Stream;

import static com.challenge.transactions.TestUtils.mapper;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class TransactionStatisticsControllerTest {
    private static final String TRANSACTIONS_ENDPOINT = "/transactions";
    private static final String STATISTICS_ENDPOINT = "/statistics";
    private static final Transaction TRANSACTION = new Transaction(Instant.now().toEpochMilli(), 42);

    private Statistics statistics;

    private MockMvc mockMvc;
    @Mock
    private TransactionStatisticsService service;

    @InjectMocks
    private TransactionStatisticsController controller;

    @Before
    public void setup() {
        DoubleSummaryStatistics summaryStatistics = new DoubleSummaryStatistics();
        summaryStatistics.accept(TRANSACTION.getAmount());
        statistics = Statistics.of(summaryStatistics);

        mockMvc = standaloneSetup(findControllers())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper()))
                .build();
    }


    @Test
    public void testPostValidTransaction() throws Exception {
        mockMvc.perform(post(TRANSACTIONS_ENDPOINT).contentType(APPLICATION_JSON)
                .content(mapper().writeValueAsString(TRANSACTION))).andExpect(status().is(201));

        verify(service).persist(TRANSACTION);
    }

    @Test
    public void testPostInvalidTransaction() throws Exception {
        doThrow(OutOfDateTransactionException.class).when(service).persist(TRANSACTION);
        mockMvc.perform(post(TRANSACTIONS_ENDPOINT).contentType(APPLICATION_JSON)
                .content(mapper().writeValueAsString(TRANSACTION))).andExpect(status().is(204));
    }

    @Test
    public void testGetStatistics() throws Exception {
        when(service.aggregate()).thenReturn(statistics);
        mockMvc.perform(get(STATISTICS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.max", is(TRANSACTION.getAmount())))
                .andExpect(jsonPath("$.min", is(TRANSACTION.getAmount())))
                .andExpect(jsonPath("$.sum", is(TRANSACTION.getAmount())))
                .andExpect(jsonPath("$.average", is(TRANSACTION.getAmount())));
    }

    private Object[] findControllers() {
        return Stream.of(getClass().getDeclaredFields())
                .filter(TransactionStatisticsControllerTest::isRestControllerField)
                .map(this::fieldValue)
                .collect(toList())
                .toArray();
    }

    private Object fieldValue(Field field) {
        return getField(this, field.getName());
    }

    private static boolean isRestControllerField(Field field) {
        return field.getType().getAnnotation(RestController.class) != null;
    }
}