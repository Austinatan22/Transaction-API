package com.acme.transactions.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

@Service
public class ExchangeRateService {
  private final WebClient http;
  private final String baseCurrency;
  private final String urlTemplate;

  public ExchangeRateService(WebClient http,
                             @Value("${app.base-currency:CHF}") String baseCurrency,
                             @Value("${app.fx-url-template}") String urlTemplate) {
    this.http = http; this.baseCurrency = baseCurrency; this.urlTemplate = urlTemplate;
  }

  public String base() { return baseCurrency; }

  public Map<String, BigDecimal> latestRates() {
    String url = urlTemplate.replace("{base}", baseCurrency);
    var resp = http.get().uri(url)
      .retrieve()
      .onStatus(HttpStatusCode::isError, r -> r.createException().map(RuntimeException::new))
      .bodyToMono(Rates.class)
      .block();
    return resp != null && resp.rates != null ? resp.rates : Map.of();
  }

  public BigDecimal toBase(String currency, BigDecimal amount) {
    if (currency == null || amount == null) return BigDecimal.ZERO;
    if (currency.equalsIgnoreCase(baseCurrency)) return amount;
    var rates = latestRates();
    var rate = rates.get(currency.toUpperCase());
    if (rate == null || BigDecimal.ZERO.compareTo(rate) == 0) return amount;
    // rates map is "1 BASE = rate OTHER" → OTHER to BASE is amount / rate
    return amount.divide(rate, MathContext.DECIMAL64);
  }

  static final class Rates {
    public Map<String, BigDecimal> rates;
  }
}
