package com.github.idelstak.friction.core.readmodel;

import com.github.idelstak.friction.core.cluster.*;
import com.github.idelstak.friction.core.observation.*;

public final class ProjectionSink {

    private final FrictionSummaryStore summary;
    private final ObservationDetailStore detail;
    private final TextValue text;

        public ProjectionSink(FrictionSummaryStore summary, ObservationDetailStore detail, TextValue text
    ) {
        this.summary = summary;
        this.detail = detail;
        this.text = text;
    }

        public void save(FrictionCluster cluster, ObservationInput input) {
        var snapshot = cluster.metrics();
        summary.save(new FrictionSummary(
          cluster.id(),
          cluster.descriptor(),
          snapshot.activity(),
          snapshot.trend()
        ));
        detail.save(new ObservationDetail(
          input.observationId(),
          cluster.id(),
          new DetailProvenance(
            input.provenance().uri(),
            input.provenance().timestamp().orElseThrow(() ->
              new IllegalStateException("timestamp missing after validation")),
            new AnonymousAuthor()
          ),
          text.excerpt(input.content())
        ));
    }
}
