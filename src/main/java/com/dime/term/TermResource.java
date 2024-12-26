package com.dime.term;

import io.micrometer.core.annotation.Counted;
import io.quarkus.hal.HalCollectionWrapper;
import io.quarkus.hal.HalEntityWrapper;
import io.quarkus.hal.HalService;
import io.quarkus.logging.Log;
import io.quarkus.resteasy.reactive.links.InjectRestLinks;
import io.quarkus.resteasy.reactive.links.RestLink;
import io.quarkus.resteasy.reactive.links.RestLinkType;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.common.util.RestMediaType;

import java.util.List;

@Path("api/v1/terms")
@Tag(name = "Terms", description = "Manage Terms")
@Produces(MediaType.APPLICATION_JSON)
public class TermResource {

  @Inject
  TermProducer producer;

  @Inject
  HalService halService;

  @Inject
  private TermMapper termMapper;

  @POST
  @Path("/add")
  @Counted(value = "addTerm")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addTerm(Term term) {
    if (term == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    Log.info("Received a new term: " + term.getWord());
    try {
      producer.sendToKafka(term);
      return Response.status(Response.Status.CREATED).entity(term).build();
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Error storing term: " + e.getMessage())
          .build();
    }
  }

  /*
   * curl -X GET http://localhost:8080/api/v1/terms/word
   */
  @GET
  @Path("{word: [a-zA-Z]+}")
  @RestLink(rel = "self-by-word")
  @Produces({MediaType.APPLICATION_JSON, RestMediaType.APPLICATION_HAL_JSON})
  @InjectRestLinks(RestLinkType.INSTANCE)
  @Operation(summary = "Get term by word")
  public HalEntityWrapper<TermRecord> getTermByWord(@PathParam("word") String word) {
    return null;
  }

  @GET
  @Path("/{id: \\d+}")
  @RestLink(rel = "self")
  @Produces({MediaType.APPLICATION_JSON, RestMediaType.APPLICATION_HAL_JSON})
  @InjectRestLinks(RestLinkType.INSTANCE)
  @Operation(summary = "Get term by id")
  public HalEntityWrapper<TermRecord> getTermById(@PathParam("id") int id) {
    return null;
  }

  @GET
  @Path("all")
  @Counted(value = "getAllTerms")
  public Response getAll() {
    return Response.ok(producer.getAllTerms()).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON, RestMediaType.APPLICATION_HAL_JSON})
  @Operation(summary = "List all terms")
  public HalCollectionWrapper<TermRecord> listAllTerms() {
    List<Term> terms = producer.getAllTerms().stream().toList();
    List<TermRecord> termRecords = terms.stream().map(termMapper::toRecord).toList();
    return halService.toHalCollectionWrapper(termRecords, "terms", TermRecord.class);
  }

}