package com.dime.term;

import com.dime.client.TermServiceClient;
import com.dime.model.TermRecord;
import com.dime.wordsapi.WordsApiService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TermService {

  @RestClient
  @Inject
  private TermServiceClient termServiceClient;

  @Inject
  private TermProducer termProducer;

  @RestClient
  @Inject
  private WordsApiService wordsApiService;

  @ConfigProperty(name = "term-service.max-retries")
  private int maxRetries;

  @ConfigProperty(name = "term-service.retry-interval-millis")
  private long retryIntervalMillis;

  /**
   * This method returns the term by its word. If the term is not found in the
   * termServiceClient, it retrieves the term from the wordsApiService and sends
   * it to the termProducer to be persisted in the database.
   * The method retries to retrieve the term from the termServiceClient up to 5
   * times with a 1 second interval between retries.
   * If the term is not found after the retries, the method returns an empty
   * Optional.
   */
  public Optional<TermRecord> getTermByWord(String word) {

    // Retry loop for retrieving TermRecord
    for (int retry = 0; retry < maxRetries; retry++) {
      Log.info("Retrieving term for word: [" + word + "]");
      Log.info("Retry: " + retry);
      try {
        Optional<TermRecord> term = termServiceClient.getTermByWord(word);
        if (term.isPresent()) {
          Log.info("Term found in termServiceClient for word: [" + word + "]");
          return term;
        }
      } catch (RuntimeException e) {
        Log.warn("Error retrieving term from termServiceClient", e);
      }

      Log.warn("Term not found in termServiceClient for word: [" + word + "]");
      // If the term isn't found on the first attempt, retrieve it from the API
      if (retry == 0) {
        Term termFromApi = wordsApiService.findByWord(word);
        if (termFromApi == null) {
          Log.warn("Error: Term not found in wordsApiService for word: [" + word + "]");
          return Optional.empty();
        }

        termProducer.sendToKafka(termFromApi);
      }

      // Wait before the next retry
      try {
        Thread.sleep(retryIntervalMillis);
      } catch (InterruptedException e) {
        Log.error("Error waiting for term to be persisted", e);
        Thread.currentThread().interrupt(); // Restore the interrupted status
        return Optional.empty();
      }
    }

    Log.warn("Max retries reached for retrieving term: [" + word + "]");
    return Optional.empty();
  }

  /**
   * This method retrieves a term from the termServiceClient by its id.
   */
  public Optional<TermRecord> getTermById(int id) {
    return termServiceClient.getTermById(id);
  }

  /**
   * This method returns a list of all terms persisted in the database.
   */
  public Optional<List<TermRecord>> listAllTerms() {
    return termServiceClient.listAllTerms();
  }

  /**
   * This method deletes a term from the database by its word.
   */
  public void deleteTerm(String word) {
    termServiceClient.deleteTerm(word);
  }

  /**
   * This method deletes a term from the database by its id.
   */
  public void deleteTermById(int id) {
    termServiceClient.deleteTermById(id);
  }
}
