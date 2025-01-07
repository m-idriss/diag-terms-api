package com.dime.client;

import com.dime.exceptions.GenericError;
import com.dime.model.TermRecord;
import io.quarkus.logging.Log;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RegisterRestClient(configKey = "term-service")
@ApplicationScoped
@Path("/api/v1/terms")
public interface TermServiceClient {

  @GET
  @Path("/{word}")
  @Produces(MediaType.APPLICATION_JSON)
  Optional<TermRecord> getTermByWord(@PathParam("word") String word);

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  Optional<TermRecord> getTermById(@PathParam("id") int id);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  Optional<List<TermRecord>> listAllTerms();

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Optional<TermRecord> createTerm(TermRecord termRecord);

  @DELETE
  @Path("/{word}")
  void deleteTerm(@PathParam("word") String word);

  @DELETE
  @Path("/{id}")
  void deleteTermById(@PathParam("id") int id);

  @ClientExceptionMapper
  static RuntimeException toException(Response response) {
    if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
      Log.warn("Term not found in term-service");
      return new NotFoundException("Term not found in term-service");
    }
    return GenericError.FAILED_DEPENDENCY.exWithArguments(Map.of("code", response.getStatus(), "message", response.readEntity(String.class)));
  }
}
