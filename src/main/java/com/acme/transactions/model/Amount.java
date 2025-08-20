package com.acme.transactions.model;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Amount {
  @NotNull
  private BigDecimal amount;
  @NotNull
  private String currency;

  public Amount() { }
  public Amount(BigDecimal amount, String currency) {
    this.amount = amount; this.currency = currency;
  }
  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
}
