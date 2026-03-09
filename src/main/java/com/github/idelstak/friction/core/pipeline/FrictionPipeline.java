package com.github.idelstak.friction.core.pipeline;

import com.github.idelstak.friction.core.cluster.*;
import com.github.idelstak.friction.core.metrics.*;
import com.github.idelstak.friction.core.observation.*;
import com.github.idelstak.friction.core.readmodel.*;

public final class FrictionPipeline {

    private final ProjectionSink sink;
    private final InputCheck check;
    private final DuplicateGate duplicate;
    private final ClusterSet set;

    public FrictionPipeline(FrictionSummaryStore summaryStore, ObservationDetailStore detailStore, double intensityDecay) {
        var text = new TextValue();
        var math = new ClusterMath(text, new TokenMatch(), new TrendLine());
        sink = new ProjectionSink(summaryStore, detailStore, text);
        check = new InputCheck();
        duplicate = new DuplicateGate(text, new ShaHash());
        set = new ClusterSet(new ClusterPolicy(text, math, intensityDecay, 0.60));
    }

    public IngestResult ingest(ObservationInput input) {
        var issue = check.check(input);
        if (issue.isPresent()) {
            return new IngestResult(new IngestOutcome.ObservationRejected(issue.get()));
        }
        if (duplicate.matches(input)) {
            return new IngestResult(new IngestOutcome.DuplicateDetected("duplicate"));
        }
        var cluster = set.accept(input);
        sink.save(cluster, input);
        duplicate.remember(input);
        return new IngestResult(new IngestOutcome.Accepted(cluster.id(), "accepted"));
    }
}
