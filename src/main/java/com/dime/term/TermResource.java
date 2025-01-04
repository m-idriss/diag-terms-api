package com.dime.term;

import com.dime.client.TermServiceClient;
import com.dime.exceptions.GenericError;
import io.quarkus.hal.HalCollectionWrapper;
import io.quarkus.hal.HalEntityWrapper;
import io.quarkus.hal.HalService;
import io.quarkus.resteasy.reactive.links.InjectRestLinks;
import io.quarkus.resteasy.reactive.links.RestLink;
import io.quarkus.resteasy.reactive.links.RestLinkType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.common.util.RestMediaType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/api/v1/terms")
@Tag(name = "Terms", description = "Manage Terms")
public class TermResource {

  @RestClient
  @Inject
  TermServiceClient termServiceClent;

  @Inject
  HalService halService;

  /*
   * curl -X GET http://localhost:8080/api/v1/terms/word
   */
  @GET
  @Path("{word: [a-zA-Z-]+}")
  @RestLink(rel = "self-by-word")
  @Produces({MediaType.APPLICATION_JSON, RestMediaType.APPLICATION_HAL_JSON})
  @InjectRestLinks(RestLinkType.INSTANCE)
  @Operation(summary = "Get term by word")
  public HalEntityWrapper<TermRecord> getTermByWord(@PathParam("word") String word) {
    String wordLower = word.toLowerCase();
    try {
      Optional<TermRecord> termRecord = termServiceClent.getTermByWord(wordLower);
      return termRecord.map(halService::toHalWrapper).orElseThrow(() -> GenericError.TERM_NOT_FOUND.exWithArguments(Map.of("word", word)));
    } catch (ClientWebApplicationException ex) {
      if (ex.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
        throw GenericError.TERM_NOT_FOUND.exWithArguments(Map.of("word", word));
      }
      throw ex;
    }
  }

  /*
   * curl -X GET http://localhost:8080/api/v1/terms/1
   */
  @GET
  @Path("/{id: \\d+}")
  @RestLink(rel = "self")
  @Produces({MediaType.APPLICATION_JSON, RestMediaType.APPLICATION_HAL_JSON})
  @InjectRestLinks(RestLinkType.INSTANCE)
  @Operation(summary = "Get term by id")
  public HalEntityWrapper<TermRecord> getTermById(@PathParam("id") int id) {
    try {
      TermRecord termRecord = termServiceClent.getTermById(id).orElseThrow();
      return halService.toHalWrapper(termRecord);
    } catch (ClientWebApplicationException ex) {
      if (ex.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
        throw GenericError.TERM_NOT_FOUND.exWithArguments(Map.of("id", id));
      }
      throw ex;
    }
  }

  /*
   * curl -X GET http://localhost:8080/api/v1/terms
   */
  @GET
  @RestLink(rel = "list")
  @Produces({MediaType.APPLICATION_JSON, RestMediaType.APPLICATION_HAL_JSON})
  @Operation(summary = "List all terms")
  public HalCollectionWrapper<TermRecord> listAllTerms() {
    List<TermRecord> termRecords = termServiceClent.listAllTerms().orElseThrow();
    return halService.toHalCollectionWrapper(termRecords, "terms", TermRecord.class);
  }

  /*
   * curl -X POST http://localhost:8080/api/v1/terms -H 'Content-Type: application/json' -d '{"word":"example"}'
   */
  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces({MediaType.APPLICATION_JSON, RestMediaType.APPLICATION_HAL_JSON})
  @Transactional
  @Operation(summary = "Create term")
  public Response createTerm(TermRecord termRecord) {
    TermRecord termRecordSaved = termServiceClent.createTerm(termRecord).orElseThrow();
    return Response.status(Response.Status.CREATED).entity(halService.toHalWrapper(termRecordSaved)).build();
  }


  /*
   * curl -X DELETE http://localhost:8080/api/v1/terms/word
   */
  @DELETE
  @Path("/{word}")
  @Transactional
  @Operation(summary = "Delete term by word")
  public Response deleteTerm(String word) {
    String wordLower = word.toLowerCase();
    termServiceClent.deleteTerm(wordLower);
    return Response.noContent().build();
  }

  /*
   * curl -X DELETE http://localhost:8080/api/v1/terms/1
   */
  @DELETE
  @Path("/{id: \\d+}")
  @Transactional
  @Operation(summary = "Delete term by id")
  public Response deleteTermById(@PathParam("id") int id) {
    termServiceClent.deleteTermById(id);
    return Response.noContent().build();
  }

}
