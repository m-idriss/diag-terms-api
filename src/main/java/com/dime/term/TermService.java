package com.dime.term;

import com.dime.client.TermServiceClient;
import com.dime.model.TermRecord;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
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

  /**
   * Retrieves a term by its word.
   * <p>
   * The method first attempts to fetch the term from the `termServiceClient`.
   * If the term is not found, it sends the word to `termProducer` for persistence
   * and retries fetching the term up to 5 times with a 1-second interval between retries.
   * If all retries fail, it falls back to returning an empty `Optional`.
   * </p>
   *
   * @param word the word for which the term is to be retrieved
   * @return an {@code Optional<TermRecord>} containing the term if found, or empty otherwise
   */
  public Optional<TermRecord> getTermByWord(String word) {
    Log.infof("Attempting to retrieve term for word: [%s]", word);
    try {
      Optional<TermRecord> term = termServiceClient.getTermByWord(word);
      if (term.isPresent()) {
        Log.infof("Term found in termServiceClient for word: [%s]", word);
        return term;
      }
    } catch (NotFoundException e) {
      Log.infof("Term not found in termServiceClient for word: [%s]", word);
      termProducer.sendToKafka(word);
      return attemptGetTermByWord(word);
    }

    Log.infof("Term retrieval failed for word: [%s], returning empty result.", word);
    return Optional.empty();
  }

  /**
   * Attempts to retrieve a term by its word, with retry and fallback mechanisms.
   *
   * @param word the word for which the term is to be retrieved
   * @return an {@code Optional<TermRecord>} containing the term if found
   * @throws NotFoundException if the term is not found after retries
   */
  @Retry(maxRetries = 9, delay = 60)
  @Fallback(fallbackMethod = "getTermByWordFallback")
  public Optional<TermRecord> attemptGetTermByWord(String word) {
    Optional<TermRecord> term = termServiceClient.getTermByWord(word);

    if (term.isPresent()) {
      Log.infof("Term successfully retrieved from termServiceClient for word: [%s]", word);
      return term;
    }

    Log.infof("Retry failed: Term not found in termServiceClient for word: [%s]", word);
    throw new NotFoundException("Term not found for word: [" + word + "]");
  }

  /**
   * Fallback method invoked when retries are exhausted or an error occurs.
   *
   * @param word the word for which the term retrieval failed
   * @return an empty {@code Optional<TermRecord>}
   */
  @SuppressWarnings("unused")
  private Optional<TermRecord> getTermByWordFallback(String word) {
    Log.infof("Fallback triggered for word: [%s]. Returning empty result.", word);
    return Optional.empty();
  }

  /**
   * This method retrieves a term from the termServiceClient by its id.
   */
  public Optional<TermRecord> getTermById(int id) {
    Log.info("Retrieving term from database by id: [" + id + "]");
    return termServiceClient.getTermById(id);
  }

  /**
   * This method returns a list of all terms persisted in the database.
   */
  public Optional<List<TermRecord>> listAllTerms() {
    Log.info("Retrieving all terms from database");
    return termServiceClient.listAllTerms();
  }

  /**
   * This method deletes a term from the database by its word.
   */
  public void deleteTerm(String word) {
    Log.info("Deleting term from database by word: [" + word + "]");
    termServiceClient.deleteTerm(word);
  }

  /**
   * This method deletes a term from the database by its id.
   */
  public void deleteTermById(int id) {
    Log.info("Deleting term from database by id: [" + id + "]");
    termServiceClient.deleteTermById(id);
  }
}
