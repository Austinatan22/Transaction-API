package com.acme.transactions.repo;

import com.acme.transactions.model.Transaction;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryMonthlyStore {

  private final Map<String, CopyOnWriteArrayList<Transaction>> byCustomerMonth = new ConcurrentHashMap<>();

  private static String key(String customerId, YearMonth ym) {
    return customerId + ":" + ym;
  }

  public void add(Transaction tx) {
    if (tx == null || tx.getCustomerId() == null || tx.getValueDate() == null) return;
    var ym = YearMonth.from(tx.getValueDate());
    byCustomerMonth.computeIfAbsent(key(tx.getCustomerId(), ym), k -> new CopyOnWriteArrayList<>()).add(tx);
  }

  public List<Transaction> list(String customerId, YearMonth ym) {
    var list = byCustomerMonth.getOrDefault(key(customerId, ym), new CopyOnWriteArrayList<>());
    var copy = new ArrayList<>(list);
    copy.sort(Comparator.comparing(Transaction::getValueDate).thenComparing(Transaction::getId));
    return copy;
  }
}
