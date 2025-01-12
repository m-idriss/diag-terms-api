package com.dime.media;

import com.dime.client.PhotoServiceClient;
import com.dime.exceptions.GenericError;
import io.quarkus.logging.Log;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.quarkus.resteasy.reactive.links.InjectRestLinks;
import io.quarkus.resteasy.reactive.links.RestLinkType;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.common.util.RestMediaType;

import java.util.Map;

@Path("/api/v1/photos")
@Tag(name = "Photos", description = "Manage Photos")
public class MediaResource {

  @RestClient
  @Inject
  private PhotoServiceClient photoServiceClient;

  @ClientExceptionMapper
  static RuntimeException toException(Response response) {
    if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
      Log.warn("Photo not found in media-service");
      return new NotFoundException("Photo not found in media-service");
    }
    return GenericError.FAILED_DEPENDENCY.exWithArguments(Map.of("code", response.getStatus(), "message", response.readEntity(String.class)));
  }

  /*
   * curl -X GET http://localhost:8080/api/v1/photos/word?page=1
   */
  @GET
  @Path("{word: [a-zA-Z]+}")
  @Produces({MediaType.APPLICATION_JSON, RestMediaType.APPLICATION_HAL_JSON})
  @InjectRestLinks(RestLinkType.INSTANCE)
  @Operation(summary = "Get photo by word, with square orientation")
  public String getPhotoByWord(@PathParam("word") String word, @DefaultValue("1") @QueryParam("page") int page) {
    Log.infof("Getting photo by word: %s, page: %d", word, page);
    return photoServiceClient.getPhotoByWord(word, page);
  }
}