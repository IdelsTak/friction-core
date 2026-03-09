package com.github.idelstak.friction.core.pipeline;

import com.github.idelstak.friction.core.observation.*;
import com.github.idelstak.friction.core.readmodel.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class FrictionPipelineTest {

  @Test
  void it_rejects_a_duplicate_observation_input() {
    var summaries = new InMemoryFrictionSummaryStore();
    var details = new InMemoryObservationDetailStore();
    var pipeline = new FrictionPipeline(summaries, details, 0.9);
    var one = input("r/javafx", "JavaFX startup crash error", Instant.parse("2026-03-09T09:00:00Z"));
    var duplicate = input("r/javafx", "JavaFX startup crash error", Instant.parse("2026-03-09T09:01:00Z"));
    pipeline.ingest(one);
    var result = pipeline.ingest(duplicate);
    assertEquals(true, result.frictionId().isEmpty(), "it cannot accept duplicate observations");
  }

  @Test
  void it_routes_a_high_confidence_observation_to_the_existing_friction() {
    var summaries = new InMemoryFrictionSummaryStore();
    var details = new InMemoryObservationDetailStore();
    var pipeline = new FrictionPipeline(summaries, details, 0.9);
    var one = input("r/javafx", "JavaFX startup crash error", Instant.parse("2026-03-09T09:00:00Z"));
    var two = input("r/javafx", "JavaFX startup crash persists", Instant.parse("2026-03-09T09:07:00Z"));
    var first = pipeline.ingest(one);
    var second = pipeline.ingest(two);
    assertEquals(first.frictionId().orElseThrow(), second.frictionId().orElseThrow(), "it cannot split highly similar observations");
  }

  @Test
  void it_routes_a_low_confidence_observation_to_a_new_friction() {
    var summaries = new InMemoryFrictionSummaryStore();
    var details = new InMemoryObservationDetailStore();
    var pipeline = new FrictionPipeline(summaries, details, 0.9);
    var one = input("r/javafx", "JavaFX startup crash error", Instant.parse("2026-03-09T09:00:00Z"));
    var three = input("r/backend", "Invoice reconciliation mismatch in ledger", Instant.parse("2026-03-09T11:00:00Z"));
    var first = pipeline.ingest(one);
    var second = pipeline.ingest(three);
    assertNotEquals(first.frictionId().orElseThrow(), second.frictionId().orElseThrow(), "it cannot cluster unrelated observations together");
  }

  @Test
  void it_leaves_trend_unavailable_for_a_single_observation() {
    var summaries = new InMemoryFrictionSummaryStore();
    var details = new InMemoryObservationDetailStore();
    var pipeline = new FrictionPipeline(summaries, details, 0.9);
    var one = input("r/javafx", "ÜI startup crash error", Instant.parse("2026-03-09T09:00:00Z"));
    var first = pipeline.ingest(one);
    var summary = summaries.find(first.frictionId().orElseThrow()).orElseThrow();
    assertTrue(summary.trend().slope().isEmpty(), "it cannot compute trend with insufficient observations");
  }

  @Test
  void it_rejects_an_observation_with_missing_required_fields() {
    var summaries = new InMemoryFrictionSummaryStore();
    var details = new InMemoryObservationDetailStore();
    var pipeline = new FrictionPipeline(summaries, details, 0.9);
    var invalid = new ObservationInput(
        "obs-invalid",
        new ObservationSource("ext-invalid", "reddit", "r/javafx"),
        new ObservationProvenance("", Optional.of(Instant.parse("2026-03-09T10:00:00Z"))),
        ""
    );
    var result = pipeline.ingest(invalid);
    assertEquals(true, result.frictionId().isEmpty(), "it cannot accept malformed observation input");
  }

  private ObservationInput input(String value, String content, Instant timestamp) {
    var id = "obs-" + value + "-" + timestamp.getEpochSecond();
    var external = "ext-" + value + "-" + timestamp.getEpochSecond();
    return new ObservationInput(
        id,
        new ObservationSource(external, "reddit", value),
        new ObservationProvenance("https://reddit.local/" + id, Optional.of(timestamp)),
        content
    );
  }
}
