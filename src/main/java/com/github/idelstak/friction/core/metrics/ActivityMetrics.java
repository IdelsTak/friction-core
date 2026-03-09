package com.github.idelstak.friction.core.metrics;

import java.time.*;

public record ActivityMetrics(int prevalence, double intensity, Duration persistence) {
}
