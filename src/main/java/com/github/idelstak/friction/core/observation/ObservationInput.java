package com.github.idelstak.friction.core.observation;

import java.util.*;

public record ObservationInput(String observationId, ObservationSource source, ObservationProvenance provenance, String content) {

  public ObservationInput {
    Objects.requireNonNull(observationId, "observationId cannot be null");
    Objects.requireNonNull(source, "source cannot be null");
    Objects.requireNonNull(provenance, "provenance cannot be null");
    Objects.requireNonNull(content, "content cannot be null");
  }
}
