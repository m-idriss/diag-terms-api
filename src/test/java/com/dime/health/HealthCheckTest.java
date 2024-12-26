package com.dime.health;

import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class HealthCheckTest {

  @Test
  void getHealth() {
    given()
        .when().get("/api/health")
        .then()
        .statusCode(200)
        .body("status", Matchers.equalTo("UP"));
  }

  @Test
  void getDatasourceHealth() {
    given()
        .when().get("/api/health/ready")
        .then()
        .statusCode(200)
        .body("status", Matchers.equalTo("UP"));
  }

}
