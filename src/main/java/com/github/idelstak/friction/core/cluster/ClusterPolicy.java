package com.github.idelstak.friction.core.cluster;

import com.github.idelstak.friction.core.metrics.*;
import com.github.idelstak.friction.core.observation.*;

public record ClusterPolicy(TextValue text, ClusterMath math, double decay, double threshold) {

}
