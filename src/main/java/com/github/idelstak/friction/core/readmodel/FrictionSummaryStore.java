package com.github.idelstak.friction.core.readmodel;

import java.util.*;

public interface FrictionSummaryStore {

    FrictionSummaryStore save(FrictionSummary summary);

    Optional<FrictionSummary> find(String frictionId);

    List<FrictionSummary> top(int limit);

    FrictionSummaryStore clear();
}
