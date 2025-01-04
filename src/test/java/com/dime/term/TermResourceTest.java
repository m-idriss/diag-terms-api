package com.dime.term;

import com.dime.client.TermServiceClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class TermResourceTest {
  private static final String TERMS_ENDPOINT = "api/v1/terms";
  private static final String DOMAIN_PATTERN = "http://[^/]+/";
  private static final String TERM_HEALTH = "{\"word\":\"health\",\"synonyms\":[\"synonym-health\"]}";
  private static final String TERM_TRY = "{\"word\":\"test-TRY\",\"synonyms\":[\"testtry\",\"testtry1\"]}";
  private static final String TERM_TEST = "{\"word\":\"test-test\",\"synonyms\":[\"testrun\"]}";


  @Inject
  @InjectMock
  @RestClient
  TermServiceClient termServiceClient;

  private static TermRecord getTestTermRecord(String word) {
    TermRecord termRecord = new TermRecord();
    termRecord.setWord(word);
    termRecord.setSynonyms(List.of("synonym-" + word));
    termRecord.setId((long) word.length());
    return termRecord;
  }

  @Test
  void getTermByWordReturnsCorrectTerm() {
    when(termServiceClient.getTermByWord("test")).thenReturn(Optional.of(getTestTermRecord("test")));

    given()
        .when().get("/api/v1/terms/test")
        .then()
        .statusCode(200)
        .body("word", equalTo("test"))
        .body("id", equalTo(4))
        .body("synonyms", hasItem("synonym-test"))
        .body("_links", allOf(
            hasKey("self-by-word"),
            hasKey("self"),
            hasKey("list")))
        .body("_links.self-by-word.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT + "/test"))
        .body("_links.self.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT + "/" + "test".length()))
        .body("_links.list.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT));
  }

  @Test
  void getTermByIdReturnsCorrectTerm() {
    when(termServiceClient.getTermById(4)).thenReturn(Optional.of(getTestTermRecord("test")));

    given()
        .when().get("/api/v1/terms/4")
        .then()
        .statusCode(200)
        .body("word", equalTo("test"))
        .body("id", equalTo(4))
        .body("synonyms", hasItem("synonym-test"))
        .body("_links", allOf(
            hasKey("self-by-word"),
            hasKey("self"),
            hasKey("list")))
        .body("_links.self-by-word.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT + "/test"))
        .body("_links.self.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT + "/" + "test".length()))
        .body("_links.list.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT));
  }

  @Test
  void listAllTermsReturnsCorrectTerms() {
    when(termServiceClient.listAllTerms()).thenReturn(Optional.of(List.of(getTestTermRecord("test"), getTestTermRecord("try"))));

    given()
        .when().get("/api/v1/terms")
        .then()
        .statusCode(200)
        .body("_embedded", hasKey("terms"))
        .body("_embedded.terms", hasSize(2))
        .body("_embedded.terms[0].word", equalTo("test"))
        .body("_embedded.terms[0].id", equalTo(4))
        .body("_embedded.terms[0].synonyms", hasItem("synonym-test"))
        .body("_embedded.terms[0]._links.self-by-word.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT + "/test"))
        .body("_embedded.terms[0]._links.self.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT + "/" + "test".length()))
        .body("_embedded.terms[0]._links.list.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT))
        .body("_embedded.terms[1].word", equalTo("try"))
        .body("_embedded.terms[1].id", equalTo(3))
        .body("_embedded.terms[1].synonyms", hasItem("synonym-try"))
        .body("_embedded.terms[1]._links.self-by-word.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT + "/try"))
        .body("_embedded.terms[1]._links.self.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT + "/" + "try".length()))
        .body("_embedded.terms[1]._links.list.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT))
        .body("_links.list.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT));
  }

  @Test
  void deleteTermByWord() {
    given()
        .when().delete("/api/v1/terms/test")
        .then()
        .statusCode(204);
  }

  @Test
  void deleteTermById() {
    given()
        .when().delete("/api/v1/terms/4")
        .then()
        .statusCode(204);
  }

  @Test
  void createTerm() {
    when(termServiceClient.createTerm(ArgumentMatchers.any(TermRecord.class))).thenReturn(Optional.of(getTestTermRecord("health")));

    given()
        .body(TERM_HEALTH)
        .header("Content-Type", "application/json")
        .when().post("/api/v1/terms")
        .then()
        .statusCode(201)
        .body("word", equalTo("health"))
        .body("id", equalTo(6))
        .body("synonyms", hasItem("synonym-health"))
        .body("_links", allOf(
            hasKey("self-by-word"),
            hasKey("self"),
            hasKey("list")))
        .body("_links.self-by-word.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT + "/health"))
        .body("_links.self.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT + "/" + "health".length()))
        .body("_links.list.href", matchesPattern(DOMAIN_PATTERN + TERMS_ENDPOINT));
  }

}
