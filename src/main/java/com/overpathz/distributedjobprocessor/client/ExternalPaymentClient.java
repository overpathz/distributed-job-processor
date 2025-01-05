package com.overpathz.distributedjobprocessor.client;

import com.overpathz.distributedjobprocessor.entity.PaymentIntent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ExternalPaymentClient {

    /* Simulate the network call */

    private final Map<Long, Boolean> prevTaskIdMap = new ConcurrentHashMap<>();

    @CircuitBreaker(name = "externalServiceCircuitBreaker", fallbackMethod = "fallbackForCircuitBreaker")
    @Retry(name = "externalServiceRetry")
    public boolean sendPayment(PaymentIntent intent) {
        if (Boolean.TRUE.equals(prevTaskIdMap.get(intent.getId()))) {
            log.info("Retry was triggered on payment={}", intent);
        }
        try {
            Thread.sleep(50);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted");
        }
    }

    // The fallback method signature must match the original method’s signature + Throwable
    public boolean fallbackForCircuitBreaker(PaymentIntent intent, Throwable t) {
        // This is called if circuit is open or retries are exhausted
        // Return false => treat as failed
        return false;
    }
}
