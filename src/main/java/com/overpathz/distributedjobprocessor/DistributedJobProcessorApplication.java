package com.overpathz.distributedjobprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DistributedJobProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedJobProcessorApplication.class, args);
    }

}
