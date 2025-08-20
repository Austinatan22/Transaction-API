package com.acme.transactions.ingest;

import com.acme.transactions.model.Transaction;
import com.acme.transactions.repo.InMemoryMonthlyStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionIngestion {

  private final ObjectMapper mapper;
  private final InMemoryMonthlyStore store;

  public TransactionIngestion(ObjectMapper mapper, InMemoryMonthlyStore store) {
    this.mapper = mapper;
    this.store = store;
  }

  @KafkaListener(
    topics = "${app.kafka.input-topic:transactions}",
    groupId = "${app.kafka.group-id:transactions-api}"
)
  public void consume(ConsumerRecord<String, String> record) {
    try {
      Transaction tx = mapper.readValue(record.value(), Transaction.class);
      store.add(tx);
    } catch (Exception ignored) {
    }
  }
}
