package com.overpathz.distributedjobprocessor.config;

import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RetryConfig {

    @Bean
    public RetryRegistry retryRegistry() {
        log.info("Initializing retry registry");

        RetryRegistry registry = RetryRegistry.ofDefaults();

        registry.retry("externalServiceRetry").getEventPublisher()
            .onRetry(event -> log.info("Retry attempt #{} for '{}'",
                                          event.getNumberOfRetryAttempts(), 
                                          event.getName()))
            .onSuccess(event -> log.info("Retry successful for '{}'", event.getName()))
            .onError(event -> log.error("Retry failed for '{}'", event.getName(), event.getLastThrowable()));

        return registry;
    }
}
