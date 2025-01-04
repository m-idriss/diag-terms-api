package com.dime.exceptions;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@QuarkusTest
public class GenericExceptionHandlerTest {

  @InjectMocks
  private GenericExceptionHandler genericExceptionHandler;

  @Mock
  private UriInfo uriInfo;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testToResponse_WithWordNotFound() {
    GenericErrorResponse errorResponse = GenericError.WORD_NOT_FOUND;
    Map<String, Object> messageArguments = Map.of("word", "toto");
    GenericException ex = new GenericException(errorResponse, messageArguments);

    when(uriInfo.getRequestUri()).thenReturn(URI.create("http://localhost:8080/api/v1/terms/word"));

    Response response = genericExceptionHandler.toResponse(ex);

    assertNotNull(response);
    assertEquals(404, response.getStatus());
    LinkedHashMap<?, ?> entity = (LinkedHashMap<?, ?>) response.getEntity();
    assertEquals(404, entity.get(GenericConstants.STATUS));
    assertEquals("WORD_NOT_FOUND", entity.get(GenericConstants.ERROR_KEY));
    assertEquals("http://localhost:8080/api/v1/terms/word", entity.get(GenericConstants.PATH));
    assertEquals("Term with word [toto] do not exist", entity.get(GenericConstants.MESSAGE));

  }

}
