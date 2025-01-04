package com.dime.client;

import com.dime.term.TermRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
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
}