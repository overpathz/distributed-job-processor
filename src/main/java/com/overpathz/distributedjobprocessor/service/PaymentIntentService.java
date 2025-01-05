package com.overpathz.distributedjobprocessor.service;

import com.overpathz.distributedjobprocessor.client.ExternalPaymentClient;
import com.overpathz.distributedjobprocessor.entity.PaymentIntent;
import com.overpathz.distributedjobprocessor.repository.PaymentIntentRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PaymentIntentService {
    private final PaymentIntentRepository paymentIntentRepository;
    private final ExternalPaymentClient externalPaymentClient;
    private final BatchUpdater batchUpdater;
    private final Timer batchProcessingTimer;

    @Autowired
    public PaymentIntentService(PaymentIntentRepository paymentIntentRepository,
                                ExternalPaymentClient externalPaymentClient,
                                MeterRegistry meterRegistry,
                                BatchUpdater batchUpdater) {
        this.paymentIntentRepository = paymentIntentRepository;
        this.externalPaymentClient = externalPaymentClient;
        this.batchUpdater = batchUpdater;

        this.batchProcessingTimer = meterRegistry.timer("payment.batch.processing.time");
    }

    /**
     * 1) Lock a batch of rows in a short transaction & mark them IN_PROGRESS.
     */
    @Transactional
    public List<PaymentIntent> fetchAndLockBatch(int batchSize) {
        List<PaymentIntent> batch = paymentIntentRepository.lockBatchForProcessing(batchSize);
        for (PaymentIntent intent : batch) {
            intent.setStatus("IN_PROGRESS");
        }
        // They will be persisted as IN_PROGRESS automatically upon commit
        return batch;
    }

    /**
     * 2) Process each row outside of transaction & update status in separate transaction.
     */
    public void processBatch(List<PaymentIntent> batch) {
        log.info("Starting batch processing. New image");
        long startTime = System.currentTimeMillis();
        // Call external service outside the transaction
        for (PaymentIntent intent : batch) {
            boolean success = externalPaymentClient.sendPayment(intent);
            intent.setStatus(success ? "PROCESSED" : "FAILED");
        }
        // Update statuses in a new short transaction
        long startUpdateBatchStatus = System.currentTimeMillis();
        batchUpdater.updateBatchStatus(batch); // not good, but leave for demo
        log.info("Update batch status in={} ms", System.currentTimeMillis() - startUpdateBatchStatus);
        log.info("Finishing batch processing. Took {} ms", System.currentTimeMillis() - startTime);
    }
}
