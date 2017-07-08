package com.challenge.transactions;

import com.challenge.transactions.model.Statistics;
import com.challenge.transactions.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

import static com.challenge.transactions.TestUtils.mapper;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DummyApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "interval.milliseconds=600000",
})
public class IntegrationTest {

    private static final long NOW = Instant.now().toEpochMilli();
    private static final long ONE_MINUTE = 60 * 1000;
    private static final long OLD = NOW - 20 * ONE_MINUTE;
    private static final long FUTURE = NOW + 20 * ONE_MINUTE;

    private static final String URL_TEMPLATE = "http://localhost:%d";
    @Value("${local.server.port}")
    protected int port;
    private HttpHeaders requestHeaders;
    private String endpoint;
    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        endpoint = String.format(URL_TEMPLATE, port);
        requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(APPLICATION_JSON);
        requestHeaders.setAccept(newArrayList(APPLICATION_JSON));
    }

    @Test
    public void integrationTest() throws JsonProcessingException, InterruptedException {
        double ten = 10;
        double twenty = 20;

        assertThat(post(new Transaction(ten, OLD)).getStatusCode(), is(HttpStatus.valueOf(204)));
        assertThat(post(new Transaction(ten, FUTURE)).getStatusCode(), is(HttpStatus.valueOf(204)));

        assertThat(post(new Transaction(ten, NOW)).getStatusCode(), is(HttpStatus.valueOf(201)));
        assertThat(post(new Transaction(ten, NOW)).getStatusCode(), is(HttpStatus.valueOf(201)));
        assertThat(post(new Transaction(ten, NOW - 2 * ONE_MINUTE)).getStatusCode(), is(HttpStatus.valueOf(201)));
        assertThat(post(new Transaction(twenty, NOW - ONE_MINUTE)).getStatusCode(), is(HttpStatus.valueOf(201)));
        assertThat(post(new Transaction(30, NOW - 10 * ONE_MINUTE + 10000)).getStatusCode(), is(HttpStatus.valueOf(201)));
        sleep(10000);

        Statistics statistics = get().getBody();

        assertThat(statistics.getAverage(), is((3 * ten + twenty) / 4));
        assertThat(statistics.getSum(), is((3 * ten + twenty)));
        assertThat(statistics.getMin(), is(ten));
        assertThat(statistics.getMax(), is(twenty));
        assertThat(statistics.getCount(), is(4L));

    }

    private ResponseEntity<Void> post(Transaction transaction) throws JsonProcessingException {
        return restTemplate.postForEntity(endpoint + "/transactions",
                new HttpEntity<>(mapper().writeValueAsString(transaction),
                        requestHeaders), Void.class);
    }

    private ResponseEntity<Statistics> get() {
        return restTemplate.getForEntity(endpoint + "/statistics", Statistics.class);
    }


}
