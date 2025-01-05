package com.overpathz.distributedjobprocessor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService externalCallExecutor() {
        // we can tune thread numbers based on our perf tests
        return Executors.newFixedThreadPool(10);
    }
}
