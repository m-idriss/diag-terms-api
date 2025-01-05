package com.dime.wordsapi;

import com.dime.exceptions.GenericError;
import com.dime.term.Term;
import io.quarkus.logging.Log;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

@Path("/words")
@RegisterRestClient(configKey = "wordsapi")
public interface WordsApiService {

  @GET
  @Path("{word}/synonyms")
  @Produces(MediaType.APPLICATION_JSON)
  Term findByWord(@PathParam("word") String word);

  // implement health check for api @Path("words/health")
  @GET
  @Path("/health/synonyms")
  @Produces(MediaType.APPLICATION_JSON)
  String healthApi();

  @ClientExceptionMapper
  static RuntimeException toException(Response response) {
    Log.warn("Error response from wordsapi: " + response.getStatusInfo().getReasonPhrase());
    if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
      return GenericError.WORD_NOT_FOUND.exWithArguments(null);
    }
    return GenericError.FAILED_DEPENDENCY.exWithArguments(Map.of("code", response.getStatus()));
  }

}
