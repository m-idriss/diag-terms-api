package com.dime;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@QuarkusTest
class StaticContentTest {

  @TestHTTPResource("index.html")
  URL url;

  @TestHTTPResource("3dime-logo-blue-small.png")
  URL smallLogoUrl;

  @TestHTTPResource("script.js")
  URL scriptUrl;

  @ConfigProperty(name = "quarkus.swagger-ui.path")
  String swaggerPath;

  @Test
  void testBrandingLogoExists() throws IOException {
    try (InputStream in = getClass().getResourceAsStream("/META-INF/branding/logo.png")) {
      byte[] bytes = Objects.requireNonNull(in).readAllBytes();
      Assertions.assertTrue(bytes.length > 0);
    }
  }

  @Test
  void testIndexHtmlContainsLogoSmall() throws IOException {
    try (InputStream in = url.openStream()) {
      String contents = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      Assertions.assertTrue(contents.contains(String.format("<a href=\".\"><img alt=\"logo\" class=\"img-fluid\" src=\".%s\"/></a>", smallLogoUrl.getFile())));
    }
  }

  @Test
  void testIndexHtmlContainsSwaggerUiPath() throws IOException {
    try (InputStream in = url.openStream()) {
      String contents = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      Assertions.assertTrue(contents.contains(String.format("href=\"%s\"", swaggerPath)));
    }
  }

  @Test
  void testScriptJsExists() throws IOException {
    try (InputStream in = scriptUrl.openStream()) {
      byte[] bytes = in.readAllBytes();
      Assertions.assertTrue(bytes.length > 0);
    }
  }

  @Test
  void testIndexHtmlContainsScriptJs() throws IOException {
    try (InputStream in = url.openStream()) {
      String contents = new String(in.readAllBytes(), StandardCharsets.UTF_8);

      Assertions.assertTrue(contents
          .contains(String.format("<script src=\"%s\"></script>", new File(scriptUrl.getFile()).getName())));
    }
  }

  @Test
  void testScriptJsContainsApiCall() throws IOException {
    try (InputStream in = scriptUrl.openStream()) {
      String contents = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      Assertions.assertTrue(contents.contains(swaggerPath + "/v1/terms"));
    }
  }
}
