package com.dime;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class StaticContentTest {

  @ConfigProperty(name = "quarkus.application.name")
  String appName;

  @Test
  void testBrandingLogoExists() throws IOException {
    try (InputStream in = getClass().getResourceAsStream("/META-INF/branding/logo.png")) {
      byte[] bytes = Objects.requireNonNull(in).readAllBytes();
      Assertions.assertTrue(bytes.length > 0);
    }
  }

  @Test
  @Deprecated
  void testRootUrlShowsSwaggerUI() {
    given()
        .when().get("/")
        .then()
        .statusCode(200)
        .body(containsString("swaggerUiLogoLink"))
        .body(containsString(appName));
  }
}
