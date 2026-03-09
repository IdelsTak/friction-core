package com.github.idelstak.friction.core.observation;

import java.time.*;
import java.util.*;

public record ObservationProvenance(String uri, Optional<Instant> timestamp) {

  public ObservationProvenance {
    Objects.requireNonNull(uri, "uri cannot be null");
    Objects.requireNonNull(timestamp, "timestamp cannot be null");
  }
}
