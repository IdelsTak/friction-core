package com.github.idelstak.friction.core.readmodel;

import com.github.idelstak.friction.core.metrics.*;

public record FrictionSummary(String frictionId, String descriptor, ActivityMetrics activity, TrendMetrics trend) {
}
