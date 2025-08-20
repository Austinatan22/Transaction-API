package com.acme.transactions.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Cursors {
  public static String encodeOffset(int offset) {
    return Base64.getUrlEncoder().withoutPadding()
      .encodeToString(("o:" + offset).getBytes(StandardCharsets.UTF_8));
  }
  public static int decodeOffset(String cursor) {
    if (cursor == null || cursor.isBlank()) return 0;
    try {
      var s = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
      if (!s.startsWith("o:")) return 0;
      return Integer.parseInt(s.substring(2));
    } catch (Exception e) { return 0; }
  }
}
