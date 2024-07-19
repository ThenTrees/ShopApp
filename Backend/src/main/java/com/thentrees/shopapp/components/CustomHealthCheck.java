package com.thentrees.shopapp.components;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthCheck implements HealthIndicator {

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {

        try {
            String computerName = InetAddress.getLocalHost().getHostName();
            return Health.up().withDetail("computerName", computerName).build(); // code: 200
        } catch (UnknownHostException e) {
            //                throw new RuntimeException(e);
            return Health.down().withDetail("Error", e.getMessage()).build();
        }
    }
}
