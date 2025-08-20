package com.acme.transactions.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateServiceTest {

    static class StubFx extends ExchangeRateService {
        StubFx(String base) { super(null, base, "ignored"); }
        @Override public Map<String, BigDecimal> latestRates() {
            return Map.of("USD", new BigDecimal("0.90"), "CHF", BigDecimal.ONE);
        }
    }

    @Test
    void toBaseReturnsSameAmountWhenAlreadyBase() {
        var fx = new StubFx("CHF");
        assertEquals(new BigDecimal("100"), fx.toBase("CHF", new BigDecimal("100")));
    }

    @Test
    void toBaseConvertsUsingLatestRates() {
        var fx = new StubFx("CHF");
        BigDecimal chf = fx.toBase("USD", new BigDecimal("100"));
        assertTrue(chf.compareTo(new BigDecimal("111.11")) >= 0);
    }
}
