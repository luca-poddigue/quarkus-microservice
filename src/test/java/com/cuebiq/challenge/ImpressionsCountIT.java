package com.cuebiq.challenge;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class ImpressionsCountIT {

    private static final String IMPRESSIONS_BY_DEVICE_PATH = "/api/impressionsCount/byDevice";
    private static final String IMPRESSIONS_BY_TIMESPAN_PATH = "/api/impressionsCount/byTimeSpan/{timeSpanId}";
    private static final String IMPRESSIONS_BY_US_STATE_PATH = "/api/impressionsCount/byUsState";

    @Test
    void testImpressionsByDeviceEndpoint() {
        given()
                .when().get(IMPRESSIONS_BY_DEVICE_PATH)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(is("{4493=1, 2425=1, 6581=1}"));
    }

    @Test
    void testImpressionsByTimeSpanEndpoint() {
        given()
                .pathParam("timeSpanId", "dow")
                .when().get(IMPRESSIONS_BY_TIMESPAN_PATH)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(is("{3=1, 6=2}"));
    }

    @Test
    void testImpressionsByUsStateEndpoint() {
        given()
                .when().get(IMPRESSIONS_BY_US_STATE_PATH)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(is("{Ohio=1}"));
    }
}
