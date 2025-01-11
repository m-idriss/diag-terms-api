package com.dime.term;

import io.micrometer.common.util.StringUtils;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class TermProducer {

  @Inject
  @Channel("terms-feed")
  Emitter<String> emitter;

  /**
   * This method sends a termRecord to the Kafka topic.
   */
  public void sendToKafka(String word) {
    if (StringUtils.isBlank(word)) {
      throw new IllegalArgumentException("Word cannot be null or empty");
    }
    try {
      emitter.send(word);
      Log.infof("Sent term [%s] to Kafka", word);
    } catch (RuntimeException e) {
      Log.error("Failed to send term to Kafka", e);
    }
  }

}
