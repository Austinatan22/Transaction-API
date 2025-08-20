package com.acme.transactions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
  @Bean
  WebClient webClient() {
    return WebClient.builder()
        .exchangeStrategies(ExchangeStrategies.builder()
          .codecs(c -> c.defaultCodecs().maxInMemorySize(1_048_576))
          .build())
        .build();
  }
}
