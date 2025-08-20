package com.acme.transactions.repo;

import com.acme.transactions.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryMonthlyStoreTest {

    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    private Transaction tx(String json) throws Exception {
        return om.readValue(json, Transaction.class);
    }

    @Test
    void listByCustomerAndMonth_returnsOnlyMatchingTransactions() throws Exception {
        var store = new InMemoryMonthlyStore();

        store.add(tx("""
          {"id":"1","customerId":"P-0123456789",
           "amount":{"amount":-5,"currency":"CHF"},
           "iban":"CH1","valueDate":"2020-10-01","description":"Coffee"}"""));

        store.add(tx("""
          {"id":"2","customerId":"P-0123456789",
           "amount":{"amount":1000,"currency":"CHF"},
           "iban":"CH1","valueDate":"2020-10-02","description":"Salary"}"""));

        store.add(tx("""
          {"id":"x","customerId":"P-OTHER",
           "amount":{"amount":10,"currency":"CHF"},
           "iban":"CHX","valueDate":"2020-10-03","description":"Ignore"}"""));

        store.add(tx("""
          {"id":"y","customerId":"P-0123456789",
           "amount":{"amount":1,"currency":"CHF"},
           "iban":"CH1","valueDate":"2020-11-01","description":"Next month"}"""));

        List<Transaction> results = store.list("P-0123456789", YearMonth.of(2020, 10));

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t ->
            "P-0123456789".equals(t.getCustomerId()) &&
            t.getValueDate().getYear() == 2020 &&
            t.getValueDate().getMonthValue() == 10
        ));
    }
}
