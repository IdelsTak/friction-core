package com.github.idelstak.friction.core.observation;

import java.util.*;

public final class DuplicateGate {

  private final Set<String> keys;
  private final TextValue text;
  private final ShaHash hash;

    public DuplicateGate(TextValue text, ShaHash hash) {
    keys = new HashSet<>();
    this.text = text;
    this.hash = hash;
  }

    public boolean matches(ObservationInput input) {
    return keys.contains(uri(input))
        || keys.contains(source(input))
        || keys.contains(bucket(input));
  }

    public void remember(ObservationInput input) {
    keys.add(uri(input));
    keys.add(source(input));
    keys.add(bucket(input));
  }

    private String uri(ObservationInput input) {
    return "uri:" + input.provenance().uri();
  }

    private String source(ObservationInput input) {
    return "source:" + input.source().externalSourceId();
  }

    private String bucket(ObservationInput input) {
    var instant = input.provenance().timestamp().orElseThrow(() -> new IllegalStateException("timestamp missing after validation"));
    var interval = String.valueOf(instant.getEpochSecond() / 300);
    var material = text.normalize(input.content()) + "|" + interval;
    return "hash:" + hash.digest(material);
  }
}
