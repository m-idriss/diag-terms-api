package com.dime.exceptions;

import jakarta.ws.rs.core.Response.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum GenericError implements GenericErrorResponse {

  TERM_NOT_FOUND("ID_NOT_FOUND", Status.NOT_FOUND, "Term with id [{id}] not found"),
  TERM_CONFLICTS("ID_CONFLICT", Status.CONFLICT, "Term with id [{id}] make conflict"),
  WORD_NOT_FOUND("WORD_NOT_FOUND", Status.NOT_FOUND, "Term with word [{word}] do not exist"),
  INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", Status.INTERNAL_SERVER_ERROR,
      Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

  private final String key;
  private final Status httpStatus;
  private final String message;

  public GenericException exWithArguments(Map<String, Object> arguments) {
    return new GenericException(this, arguments);
  }
}
