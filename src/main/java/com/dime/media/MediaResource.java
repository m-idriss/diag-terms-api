package com.dime.media;

import com.dime.client.PhotoServiceClient;
import io.quarkus.resteasy.reactive.links.InjectRestLinks;
import io.quarkus.resteasy.reactive.links.RestLinkType;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.common.util.RestMediaType;

@Path("/api/v1/photos")
@Tag(name = "Photos", description = "Manage Photos")
public class MediaResource {

  @RestClient
  @Inject
  private PhotoServiceClient pictureServiceClient;

  /*
   * curl -X GET http://localhost:8080/api/v1/photos/word?page=1
   */
  @GET
  @Path("{word: [a-zA-Z]+}")
  @Produces({MediaType.APPLICATION_JSON, RestMediaType.APPLICATION_HAL_JSON})
  @InjectRestLinks(RestLinkType.INSTANCE)
  @Operation(summary = "Get photo by word")
  public String getPhotoByWord(@PathParam("word") String word, @QueryParam("page") int page) {
    return pictureServiceClient.getPhotoByWord(word, page);
  }
}