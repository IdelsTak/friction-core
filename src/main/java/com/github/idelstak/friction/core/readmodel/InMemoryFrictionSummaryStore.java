package com.github.idelstak.friction.core.readmodel;

import java.util.*;

public final class InMemoryFrictionSummaryStore implements FrictionSummaryStore {

  private final Map<String, FrictionSummary> summaries = new HashMap<>();

  @Override
  public FrictionSummaryStore save(FrictionSummary summary) {
    summaries.put(summary.frictionId(), summary);
    return this;
  }

  @Override
  public Optional<FrictionSummary> find(String frictionId) {
    return Optional.ofNullable(summaries.get(frictionId));
  }

  @Override
  public List<FrictionSummary> top(int limit) {
    List<FrictionSummary> ordered = new ArrayList<>(summaries.values());
    ordered.sort(Comparator.<FrictionSummary>comparingInt(one -> one.activity().prevalence()).reversed());
    return ordered.subList(0, Math.min(limit, ordered.size()));
  }

  @Override
  public FrictionSummaryStore clear() {
    summaries.clear();
    return this;
  }
}
