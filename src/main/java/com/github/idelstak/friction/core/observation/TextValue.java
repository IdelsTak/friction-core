package com.github.idelstak.friction.core.observation;

import java.util.*;

public final class TextValue {

    public String normalize(String value) {
    if (value == null) {
      return "";
    }
    return value.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
  }

    public String excerpt(String content) {
    var value = normalize(content);
    if (value.length() <= 180) {
      return value;
    }
    return value.substring(0, 180);
  }

    public String descriptor(String content) {
    var words = normalize(content).split(" ");
    var limit = Math.min(8, words.length);
    return String.join(" ", java.util.Arrays.copyOf(words, limit));
  }

    public Set<String> tokens(String content) {
    var normalized = normalize(content).replaceAll("[^a-z0-9 ]", " ");
    var split = normalized.split(" ");
    Set<String> tokens = new HashSet<>();
    for (var token : split) {
      if (!token.isBlank()) {
        tokens.add(token);
      }
    }
    return tokens;
  }
}
