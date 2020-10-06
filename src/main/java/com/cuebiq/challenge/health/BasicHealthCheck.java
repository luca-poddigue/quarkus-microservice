package com.cuebiq.challenge.health;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;

@Health
@ApplicationScoped
/**
 * Basic health check endpoint to verify the service status. It does not perform any special check:
 * the service can be considered healthy if it replied within a reasonable amount of time.
 */
public class BasicHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Service is up.").up().build();
    }
}