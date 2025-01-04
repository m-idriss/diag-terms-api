package com.dime.exceptions;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;

@Provider
@RequestScoped
public class GenericExceptionHandler implements ExceptionMapper<GenericException> {

  @Inject
  @Context
  private UriInfo uriInfo;

  @Override
  public Response toResponse(GenericException ex) {
    return createResponse(ex.getErrorResponse().getHttpStatus().getStatusCode(), ex.getErrorResponse().getKey(),
        ex.getMessage());
  }

  private Response createResponse(int statusCode, String key, String message) {
    LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
    Response.Status status = Response.Status.fromStatusCode(statusCode);

    attributes.put(GenericConstants.STATUS, statusCode);
    attributes.put(GenericConstants.ERROR, status);
    attributes.put(GenericConstants.ERROR_KEY, key);
    attributes.put(GenericConstants.PATH, getRequestPath());
    attributes.put(GenericConstants.MESSAGE, message);

    return Response.status(status).entity(attributes).build();
  }

  private String getRequestPath() {
    return uriInfo.getRequestUri().toString();
  }
}
