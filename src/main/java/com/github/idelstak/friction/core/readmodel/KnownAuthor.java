package com.github.idelstak.friction.core.readmodel;

import java.util.*;

public record KnownAuthor(String value) implements AuthorHandle {

  public KnownAuthor {
    Objects.requireNonNull(value, "value cannot be null");
  }
}
