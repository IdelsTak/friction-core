package com.github.idelstak.friction.core.cluster;

import com.github.idelstak.friction.core.observation.*;
import java.util.*;

public final class ClusterSet {

    private final ClusterPolicy policy;
    private final Map<String, FrictionCluster> clusters;

    public ClusterSet(ClusterPolicy policy) {
        this.policy = policy;
        clusters = new HashMap<>();
    }

    public FrictionCluster accept(ObservationInput input) {
        var cluster = pick(input).orElseGet(this::create);
        cluster.accept(input, policy.decay(), policy.math().signal(input));
        return cluster;
    }

    public String excerpt(String content) {
        return policy.text().excerpt(content);
    }

    private Optional<FrictionCluster> pick(ObservationInput input) {
        FrictionCluster best = null;
        var score = Double.NEGATIVE_INFINITY;
        for (var cluster : clusters.values()) {
            var current = cluster.score(input);
            if (current > score) {
                score = current;
                best = cluster;
            }
        }
        if (best == null) {
            return Optional.empty();
        }
        if (score >= policy.threshold()) {
            return Optional.of(best);
        }
        return Optional.empty();
    }

    private FrictionCluster create() {
        var cluster = new FrictionCluster(UUID.randomUUID().toString(), policy.math());
        clusters.put(cluster.id(), cluster);
        return cluster;
    }
}
