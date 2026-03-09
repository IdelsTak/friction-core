package com.github.idelstak.friction.core.cluster;

import com.github.idelstak.friction.core.metrics.*;
import com.github.idelstak.friction.core.observation.*;
import java.util.*;

public final class FrictionCluster {

    private final String id;
    private final ClusterMath math;
    private final List<ObservationInput> observations;
    private double raw;

    public FrictionCluster(String id, ClusterMath math) {
        this.id = id;
        this.math = math;
        observations = new ArrayList<>();
        raw = 0.0;
    }

    public String id() {
        return id;
    }

    public void accept(ObservationInput input, double decay, double signal) {
        observations.add(input);
        raw = (raw * decay) + signal;
    }

    public double score(ObservationInput input) {
        var first = observations.get(0).source().sourceType();
        return math.score(input, first, combined());
    }

    public String descriptor() {
        var latest = observations.get(observations.size() - 1);
        return math.descriptor(latest.content());
    }

    public MetricsSnapshot metrics() {
        return math.metrics(observations, raw);
    }

    private String combined() {
        var text = new StringBuilder();
        for (var observation : observations) {
            text.append(' ').append(observation.content());
        }
        return text.toString();
    }
}
