package com.acme.transactions.api;

import com.acme.transactions.model.Transaction;
import com.acme.transactions.repo.InMemoryMonthlyStore;
import com.acme.transactions.service.ExchangeRateService;
import com.acme.transactions.util.Cursors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

@RestController
@RequestMapping("/v1/transactions")
@Tag(name = "Transactions")
public class TransactionsController {

  private final InMemoryMonthlyStore store;
  private final ExchangeRateService fx;
  private final boolean securityDisabled;

  public TransactionsController(InMemoryMonthlyStore store,
                                ExchangeRateService fx,
                                @Value("${app.security.disabled:false}") boolean securityDisabled) {
    this.store = store; this.fx = fx; this.securityDisabled = securityDisabled;
  }

  @GetMapping
  @Operation(summary = "List transactions for a calendar month with pagination and credit/debit totals at current FX")
  public PageResponse list(
      @RequestParam String month,
      @RequestParam(defaultValue = "50") int pageSize,
      @RequestParam(required = false) String cursor,
      @RequestHeader(value = "X-User-Id", required = false) String userHeader,
      @AuthenticationPrincipal Jwt jwt
  ) {
    var ym = YearMonth.parse(month);
    String userId = resolveUser(userHeader, jwt);

    var all = store.list(userId, ym);
    int offset = Cursors.decodeOffset(cursor);
    if (offset < 0) offset = 0;
    int end = Math.min(offset + Math.min(Math.max(pageSize, 1), 200), all.size());
    var page = all.subList(Math.min(offset, all.size()), end);

    var base = fx.base();
    BigDecimal credit = BigDecimal.ZERO;
    BigDecimal debit  = BigDecimal.ZERO;

    for (Transaction t : page) {
      var amt = t.getAmount().getAmount();
      var ccy = t.getAmount().getCurrency();
      var inBase = fx.toBase(ccy, amt);
      if (inBase.signum() >= 0) credit = credit.add(inBase);
      else debit = debit.add(inBase.abs());
    }

    String next = end < all.size() ? Cursors.encodeOffset(end) : null;

    return PageResponse.of(page, next, base, credit, debit);
  }

  private String resolveUser(String userHeader, Jwt jwt) {
    if (!securityDisabled) return jwt.getSubject();
    if (StringUtils.hasText(userHeader)) return userHeader;
    return "P-0123456789";
  }

  public record PageResponse(
      List<Transaction> items,
      String nextCursor,
      Totals totals
  ) {
    public static PageResponse of(List<Transaction> items, String next, String base, BigDecimal credit, BigDecimal debit) {
      return new PageResponse(items, next, new Totals(base, credit, debit));
    }
  }
  public record Totals(String baseCurrency, BigDecimal credit, BigDecimal debit) { }
}
