package com.dime.exceptions;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
class GenericExceptionTest {

  @Test
  void test_getMessage_with_arguments() {
    GenericException result = GenericError.TERM_NOT_FOUND.exWithArguments(Map.of("id", 45));

    assertEquals(GenericError.TERM_NOT_FOUND, result.getErrorResponse());
    assertEquals("Term with id [45] not found", result.getMessage());
    Map<String, Object> arguments = result.getMessageArguments();
    assertEquals(1, arguments.size());
    assertEquals(45, arguments.get("id"));
  }

  @Test
  void test_getMessage_without_arguments() {
    GenericException result = GenericError.INTERNAL_SERVER_ERROR.exWithArguments(Map.of());

    assertEquals(GenericError.INTERNAL_SERVER_ERROR, result.getErrorResponse());
    assertEquals("Internal Server Error", result.getMessage());
    Map<String, Object> arguments = result.getMessageArguments();
    assertEquals(0, arguments.size());
  }

  @Test
  void test_getMessage_with_null_arguments() {
    GenericException result = GenericError.TERM_CONFLICTS.exWithArguments(null);

    assertEquals(GenericError.TERM_CONFLICTS, result.getErrorResponse());
    assertEquals("Conflict", result.getMessage());
    Map<String, Object> arguments = result.getMessageArguments();
    assertNull(arguments);
  }
}
