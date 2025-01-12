package com.dime.client;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@RegisterRestClient(configKey = "media-service")
public interface PhotoServiceClient {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ClientQueryParam(name = "per_page", value = "1")
  @ClientQueryParam(name = "orientation", value = "square")
  String getPhotoByWord(@QueryParam("query") String word, @QueryParam("page") int page);

}
