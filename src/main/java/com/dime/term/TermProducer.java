package com.dime.term;

import com.dime.exceptions.TermException;
import com.dime.model.TermRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class TermProducer {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Inject
  @Channel("terms-out")
  Emitter<Record<String, String>> emitter;

  /**
   * This method sends a termRecord to the Kafka topic.
   */
  public void sendToKafka(String word, TermRecord termRecord) {
    if (termRecord == null || termRecord.getWord() == null) {
      throw new IllegalArgumentException("Term or its properties cannot be null");
    }
    try {
      String termJson = termToJson(termRecord);
      Log.infof("Sending termRecord to Kafka: %s", termJson);
      emitter.send(Record.of(word, termJson));
      Log.infof("Term sent to Kafka: %s", termJson);
    } catch (RuntimeException e) {
      Log.error("Failed to send termRecord to Kafka", e);
    } catch (TermException e) {
      Log.error("Failed to serialize Term object to JSON", e);
    }
  }

  /**
   * This method serializes a Term object to JSON.
   */
  private String termToJson(TermRecord termRecord) throws TermException {
    try {
      return objectMapper.writeValueAsString(termRecord);
    } catch (JsonProcessingException e) {
      throw new TermException("Failed to serialize TermRecord object to JSON", e);
    }
  }

}
