package com.acme.transactions.api;

import com.acme.transactions.model.Transaction;
import com.acme.transactions.repo.InMemoryMonthlyStore;
import com.acme.transactions.service.ExchangeRateService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class TransactionsControllerTest {

    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    private InMemoryMonthlyStore store;
    private ExchangeRateService fx;
    private TransactionsController controller;

    private Transaction tx(String id, String customerId, int amount, String currency,
                           String iban, String valueDate, String description) {
        try {
            String json = """
                {
                  "id":"%s",
                  "customerId":"%s",
                  "amount":{"amount":%d,"currency":"%s"},
                  "iban":"%s",
                  "valueDate":"%s",
                  "description":"%s"
                }
                """.formatted(id, customerId, amount, currency, iban, valueDate, description);
            return om.readValue(json, Transaction.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        store = new InMemoryMonthlyStore();

        fx = Mockito.mock(ExchangeRateService.class);
        when(fx.base()).thenReturn("CHF");
        when(fx.toBase(anyString(), any(BigDecimal.class)))
                .thenAnswer(inv -> inv.getArgument(1, BigDecimal.class));

        controller = new TransactionsController(store, fx, true);

        store.add(tx("1", "P-0123456789", -5,   "CHF", "CH1", "2020-10-01", "Coffee"));
        store.add(tx("2", "P-0123456789", 1000, "CHF", "CH1", "2020-10-02", "Salary"));
        store.add(tx("999","P-OTHER",       10, "CHF", "CHX", "2020-10-02", "Ignore me"));
    }

    @Test
    void shouldListTransactionsAndComputeTotals() {
        var page = controller.list("2020-10", 50, null, "P-0123456789", null);

        assertEquals(2, page.items().size(), "should return 2 items");
        assertNull(page.nextCursor(), "no cursor when everything fits in one page");

        assertEquals("CHF", page.totals().baseCurrency());
        assertEquals(0, page.totals().credit().compareTo(new BigDecimal("1000")));
        assertEquals(0, page.totals().debit().compareTo(new BigDecimal("5")));
    }

    @Test
    void shouldPaginateWithCursor() {
        var first = controller.list("2020-10", 1, null, "P-0123456789", null);
        assertEquals(1, first.items().size());
        assertNotNull(first.nextCursor());

        var second = controller.list("2020-10", 1, first.nextCursor(), "P-0123456789", null);
        assertEquals(1, second.items().size());
        assertNull(second.nextCursor());
    }
}
