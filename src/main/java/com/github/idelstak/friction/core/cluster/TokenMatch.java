package com.github.idelstak.friction.core.cluster;

import java.util.*;

public final class TokenMatch {

    public double jaccard(Set<String> left, Set<String> right) {
    if (left.isEmpty() || right.isEmpty()) {
      return 0.0;
    }
    var intersection = new HashSet<>(left);
    intersection.retainAll(right);
    var union = new HashSet<>(left);
    union.addAll(right);
    return (double) intersection.size() / (double) union.size();
  }
}
