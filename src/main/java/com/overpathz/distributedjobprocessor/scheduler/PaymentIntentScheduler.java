package com.overpathz.distributedjobprocessor.scheduler;

import com.overpathz.distributedjobprocessor.entity.PaymentIntent;
import com.overpathz.distributedjobprocessor.service.PaymentIntentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PaymentIntentScheduler {

    private final PaymentIntentService paymentIntentService;
    private static final int BATCH_SIZE = 1000;

    @Autowired
    public PaymentIntentScheduler(PaymentIntentService paymentIntentService) {
        this.paymentIntentService = paymentIntentService;
    }

    @Scheduled(fixedRate = 5000, initialDelayString = "#{T(java.util.concurrent.ThreadLocalRandom).current().nextLong(1000, 10000)}")
    public void processTransactions() {
        log.info("Processing batch of transactions..");
        List<PaymentIntent> batch = paymentIntentService.fetchAndLockBatch(BATCH_SIZE);
        if (!batch.isEmpty()) {
            paymentIntentService.processBatch(batch);
        }
        log.info("Processed {} transactions.", batch.size());
    }
}
