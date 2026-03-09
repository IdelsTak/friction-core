package com.github.idelstak.friction.core.metrics;

import com.github.idelstak.friction.core.cluster.*;
import com.github.idelstak.friction.core.observation.*;
import java.time.*;
import java.util.*;

public final class ClusterMath {

  private final TextValue text;
  private final TokenMatch match;
  private final TrendLine line;

    public ClusterMath(TextValue text, TokenMatch match, TrendLine line) {
    this.text = text;
    this.match = match;
    this.line = line;
  }

    public double score(ObservationInput input, String first, String combined) {
    var incoming = text.tokens(input.content());
    var existing = text.tokens(combined);
    var lexical = match.jaccard(incoming, existing);
    var provenance = input.source().sourceType().equals(first) ? 0.2 : 0.0;
    return Math.min(1.0, lexical + provenance);
  }

    public double signal(ObservationInput input) {
    var size = text.tokens(input.content()).size();
    return Math.min(1.0, size / 20.0);
  }

    public String descriptor(String content) {
    return text.descriptor(content);
  }

    public MetricsSnapshot metrics(List<ObservationInput> observations, double raw) {
    var prevalence = observations.size();
    var intensity = intensity(raw);
    var timestamps = observations.stream().map(one -> one.provenance().timestamp()).flatMap(Optional::stream).toList();
    var first = timestamps.stream().min(Comparator.naturalOrder());
    var latest = timestamps.stream().max(Comparator.naturalOrder());
    Duration persistence;
    if (first.isEmpty() || latest.isEmpty() || first.get().equals(latest.get())) {
      persistence = Duration.ZERO;
    } else {
      persistence = Duration.between(first.get(), latest.get());
    }
    var trend = line.calculate(observations);
    return new MetricsSnapshot(
        new ActivityMetrics(prevalence, intensity, persistence),
        new TrendMetrics(trend.slope(), trend.confidence())
    );
  }

    private double intensity(double raw) {
    if (raw <= 0.0) {
      return 0.0;
    }
    return raw / (raw + 3.0);
  }
}
