package com.github.idelstak.friction.core.observation;

import java.util.*;

public record ObservationSource(String externalSourceId, String sourceType, String sourceValue) {

  public ObservationSource {
    Objects.requireNonNull(externalSourceId, "externalSourceId cannot be null");
    Objects.requireNonNull(sourceType, "sourceType cannot be null");
    Objects.requireNonNull(sourceValue, "sourceValue cannot be null");
  }
}
