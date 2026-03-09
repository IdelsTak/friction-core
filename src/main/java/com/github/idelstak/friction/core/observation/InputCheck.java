package com.github.idelstak.friction.core.observation;

import java.util.*;

public final class InputCheck {

    public Optional<String> check(ObservationInput input) {
    if (blank(input.observationId())) {
      return Optional.of("missing observationId");
    }
    if (blank(input.source().externalSourceId())) {
      return Optional.of("missing externalSourceId");
    }
    if (blank(input.source().sourceType())) {
      return Optional.of("missing sourceType");
    }
    if (blank(input.source().sourceValue())) {
      return Optional.of("missing sourceValue");
    }
    if (blank(input.provenance().uri())) {
      return Optional.of("missing provenanceUri");
    }
    if (input.provenance().timestamp().isEmpty()) {
      return Optional.of("missing timestamp");
    }
    if (blank(input.content())) {
      return Optional.of("missing content");
    }
    return Optional.empty();
  }

    private boolean blank(String value) {
    return value.trim().isEmpty();
  }
}
