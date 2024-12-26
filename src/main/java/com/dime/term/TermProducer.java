package com.dime.term;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.Collection;
import java.util.Collections;

@ApplicationScoped
public class TermProducer {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Inject
  @Channel("terms-out")
  Emitter<Record<String, String>> emitter;

  public void sendToKafka(Term term) {
    if (term == null || term.getWord() == null) {
      throw new IllegalArgumentException("Term or its properties cannot be null");
    }
    try {
      String termJson = termToJson(term);
      Log.infof("Sending term to Kafka: %s", termJson);
      emitter.send(Record.of(term.getWord(), termJson));
      Log.infof("Term sent to Kafka: %s", termJson);
    } catch (RuntimeException e) {
      Log.error("Failed to send term to Kafka", e);
    } catch (TermException e) {
      Log.error("Failed to serialize Term object to JSON", e);
    }
  }

  private String termToJson(Term term) throws TermException {
    try {
      return objectMapper.writeValueAsString(term);
    } catch (JsonProcessingException e) {
      throw new TermException("Failed to serialize Term object to JSON", e);
    }
  }

  public Collection<Term> getAllTerms() {
    return Collections.emptyList();
  }
}
