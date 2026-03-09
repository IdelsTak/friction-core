package com.github.idelstak.friction.core.pipeline;

import java.util.*;

public sealed interface IngestOutcome {

        String reason();

        Optional<String> friction();

        record Accepted(String frictionId, String reason) implements IngestOutcome {

        @Override
        public Optional<String> friction() {
            return Optional.of(frictionId);
        }

    }

        record DuplicateDetected(String reason) implements IngestOutcome {

        @Override
        public Optional<String> friction() {
            return Optional.empty();
        }

    }

        record ObservationRejected(String reason) implements IngestOutcome {

        @Override
        public Optional<String> friction() {
            return Optional.empty();
        }

    }
}
