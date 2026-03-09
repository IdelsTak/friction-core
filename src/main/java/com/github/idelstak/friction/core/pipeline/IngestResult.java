package com.github.idelstak.friction.core.pipeline;

import java.util.*;

public record IngestResult(IngestOutcome outcome) {

    public Optional<String> frictionId() {
    return outcome.friction();
  }

    public String reason() {
    return outcome.reason();
  }
}
