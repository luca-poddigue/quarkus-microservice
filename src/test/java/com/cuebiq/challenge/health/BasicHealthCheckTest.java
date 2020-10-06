package com.cuebiq.challenge.health;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

class BasicHealthCheckTest {

    @Test
    void whenCallingHelathCheck_shouldReportTheServiceIsUp() {
        BasicHealthCheck healthCheck = new BasicHealthCheck();
        HealthCheckResponse response = healthCheck.call();

        assertThat(response.getState(), Matchers.is(equalTo(HealthCheckResponse.State.UP)));
    }
}