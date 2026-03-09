package com.github.idelstak.friction.core.readmodel;

import java.time.*;
import java.util.*;

public record DetailProvenance(String uri, Instant timestamp, AuthorHandle authorHandle) {

  public DetailProvenance {
    Objects.requireNonNull(uri, "uri cannot be null");
    Objects.requireNonNull(timestamp, "timestamp cannot be null");
    Objects.requireNonNull(authorHandle, "authorHandle cannot be null");
  }
}
