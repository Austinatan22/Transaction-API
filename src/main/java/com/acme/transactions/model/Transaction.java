package com.acme.transactions.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class Transaction {
  @NotBlank private String id;
  @NotBlank private String customerId;
  @NotNull private Amount amount;
  @NotBlank private String iban;
  private LocalDate valueDate;
  private String description;

  public Transaction(String string, String string2, Amount amount2, String string3, LocalDate localDate, String string4) { }

  public Transaction() {
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getCustomerId() { return customerId; }
  public void setCustomerId(String customerId) { this.customerId = customerId; }
  public Amount getAmount() { return amount; }
  public void setAmount(Amount amount) { this.amount = amount; }
  public String getIban() { return iban; }
  public void setIban(String iban) { this.iban = iban; }
  public LocalDate getValueDate() { return valueDate; }
  public void setValueDate(LocalDate valueDate) { this.valueDate = valueDate; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
