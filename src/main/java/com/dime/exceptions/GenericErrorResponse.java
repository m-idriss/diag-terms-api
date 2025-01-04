package com.dime.exceptions;

import jakarta.ws.rs.core.Response.Status;

public interface GenericErrorResponse {

  String getKey();

  String getMessage();

  Status getHttpStatus();
}
