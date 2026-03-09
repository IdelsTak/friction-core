package com.github.idelstak.friction.core.readmodel;

import java.util.*;

public final class InMemoryObservationDetailStore implements ObservationDetailStore {

  private final Map<String, List<ObservationDetail>> detailsByFriction = new HashMap<>();

  @Override
  public ObservationDetailStore save(ObservationDetail detail) {
    detailsByFriction
        .computeIfAbsent(detail.frictionId(), ignored -> new ArrayList<>())
        .add(detail);
    return this;
  }

  @Override
  public List<ObservationDetail> byFriction(String frictionId) {
    return new ArrayList<>(detailsByFriction.getOrDefault(frictionId, List.of()));
  }

  @Override
  public ObservationDetailStore clear() {
    detailsByFriction.clear();
    return this;
  }
}
