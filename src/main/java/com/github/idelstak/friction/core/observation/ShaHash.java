package com.github.idelstak.friction.core.observation;

import java.nio.charset.*;
import java.security.*;

public final class ShaHash {

    public String digest(String value) {
    try {
      var hash = MessageDigest.getInstance("SHA-256");
      var bytes = hash.digest(value.getBytes(StandardCharsets.UTF_8));
      var hex = new StringBuilder();
      for (byte one : bytes) {
        hex.append(String.format("%02x", one));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 not available", exception);
    }
  }
}
