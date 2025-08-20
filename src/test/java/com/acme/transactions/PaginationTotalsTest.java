package com.acme.transactions;

import com.acme.transactions.model.Amount;
import com.acme.transactions.model.Transaction;
import com.acme.transactions.repo.InMemoryMonthlyStore;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

public class PaginationTotalsTest {

  @Test
  void storeSortsAndSlices() {
    var store = new InMemoryMonthlyStore();
    for (int i=1;i<=3;i++) {
      var t = new Transaction();
      t.setId("T"+i);
      t.setCustomerId("C1");
      t.setIban("CH1");
      t.setValueDate(LocalDate.of(2020,10,i));
      t.setAmount(new Amount(BigDecimal.valueOf(i), "CHF"));
      store.add(t);
    }
    var list = store.list("C1", YearMonth.of(2020,10));
    assertEquals(3, list.size());
    assertEquals("T1", list.get(0).getId());
  }
}
