package com.github.idelstak.friction.core.metrics;

import com.github.idelstak.friction.core.observation.*;
import java.time.*;
import java.util.*;

public final class TrendLine {

    Trend calculate(List<ObservationInput> observations) {
    Map<Long, Integer> byDay = new HashMap<>();
    for (var observation : observations) {
      if (observation.provenance().timestamp().isEmpty()) {
        continue;
      }
      var day = LocalDate.ofInstant(observation.provenance().timestamp().orElseThrow(), ZoneOffset.UTC).toEpochDay();
      byDay.merge(day, 1, Integer::sum);
    }
    if (byDay.size() < 2) {
      return new Trend(Optional.empty(), Optional.empty());
    }
    var xs = new ArrayList<>(byDay.keySet());
    xs.sort(Long::compareTo);
    var ys = xs.stream().map(day -> byDay.get(day).doubleValue()).toList();
    var meanX = xs.stream().mapToDouble(Long::doubleValue).average().orElse(0.0);
    var meanY = ys.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    var numerator = 0.0;
    var denominator = 0.0;
    for (int index = 0; index < xs.size(); index++) {
      var dx = xs.get(index) - meanX;
      numerator += dx * (ys.get(index) - meanY);
      denominator += dx * dx;
    }
    if (denominator == 0.0) {
      return new Trend(Optional.empty(), Optional.empty());
    }
    var slope = numerator / denominator;
    var total = 0.0;
    var residual = 0.0;
    for (int index = 0; index < xs.size(); index++) {
      var x = xs.get(index);
      var y = ys.get(index);
      var estimate = meanY + slope * (x - meanX);
      total += Math.pow(y - meanY, 2);
      residual += Math.pow(y - estimate, 2);
    }
    double confidence;
    if (total == 0.0) {
      confidence = 1.0;
    } else {
      confidence = Math.max(0.0, Math.min(1.0, 1.0 - (residual / total)));
    }
    return new Trend(Optional.of(slope), Optional.of(confidence));
  }
}
