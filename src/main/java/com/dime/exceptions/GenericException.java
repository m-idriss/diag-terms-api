package com.dime.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.util.Map;

@Getter
@AllArgsConstructor
public class GenericException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -3784903329806863768L;
  private final transient GenericErrorResponse errorResponse;
  private final transient Map<String, Object> messageArguments;

  @Override
  public String getMessage() {
    String message = errorResponse.getMessage();
    if (messageArguments == null) {
      return errorResponse.getHttpStatus().getReasonPhrase();
    }
    for (Map.Entry<String, Object> entry : messageArguments.entrySet()) {
      message = message.replace("{" + entry.getKey() + "}", entry.getValue().toString());
    }
    return message;
  }
}
