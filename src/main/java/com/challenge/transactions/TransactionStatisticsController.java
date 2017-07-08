package com.challenge.transactions;

import com.challenge.transactions.model.OutOfDateTransactionException;
import com.challenge.transactions.model.Statistics;
import com.challenge.transactions.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class TransactionStatisticsController {

    private final TransactionStatisticsService transactionStatisticsService;

    @Autowired
    public TransactionStatisticsController(TransactionStatisticsService transactionStatisticsService) {
        this.transactionStatisticsService = transactionStatisticsService;
    }

    @PostMapping("/transactions")
    public void postTransactions(@RequestBody Transaction transaction, HttpServletResponse response) {
        try {
            transactionStatisticsService.persist(transaction);
            response.setStatus(201);
        } catch (OutOfDateTransactionException e) {
            response.setStatus(204);
        }
    }

    @GetMapping("/statistics")
    public Statistics getStatistics() {
        return transactionStatisticsService.aggregate();
    }
}
