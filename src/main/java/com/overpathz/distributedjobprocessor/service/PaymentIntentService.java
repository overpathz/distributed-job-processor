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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@Slf4j
public class PaymentIntentService {
    private final PaymentIntentRepository paymentIntentRepository;
    private final ExternalPaymentClient externalPaymentClient;
    private final BatchUpdater batchUpdater;
    private final Timer batchProcessingTimer;
    private final ExecutorService externalCallExecutor;

    @Autowired
    public PaymentIntentService(PaymentIntentRepository paymentIntentRepository,
                                ExternalPaymentClient externalPaymentClient,
                                MeterRegistry meterRegistry,
                                BatchUpdater batchUpdater, ExecutorService externalCallExecutor) {
        this.paymentIntentRepository = paymentIntentRepository;
        this.externalPaymentClient = externalPaymentClient;
        this.batchUpdater = batchUpdater;

        this.batchProcessingTimer = meterRegistry.timer("payment.batch.processing.time");
        this.externalCallExecutor = externalCallExecutor;
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
     * Processes each PaymentIntent in parallel using the ExecutorService.
     */
    public void processBatch(List<PaymentIntent> batch) {
        log.info("Starting batch processing. New image 2");
        log.info("Starting batch processing for {}", batch.size());
        long startTime = System.currentTimeMillis();

        List<Future<ProcessingResult>> futures = new ArrayList<>(batch.size());
        for (PaymentIntent intent : batch) {
            futures.add(externalCallExecutor.submit(() -> {
                boolean success = externalPaymentClient.sendPayment(intent);
                return new ProcessingResult(intent.getId(), success);
            }));
        }

        List<Long> processedIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();

        for (Future<ProcessingResult> future : futures) {
            try {
                ProcessingResult result = future.get(); // blocking wait
                if (result.success()) {
                    processedIds.add(result.paymentId());
                } else {
                    failedIds.add(result.paymentId());
                }
            } catch (Exception e) {
                // Right now, we are not capturing the ID unless we wrap this logic properly.
                // I mean, we can wrap our Future task by some record with id to be able to know id
                //  when exception is thrown.
            }
        }

        batchUpdater.updateBatchStatus(processedIds, failedIds);

        log.info("Finishing batch processing. Took {} ms", System.currentTimeMillis() - startTime);
    }

    record ProcessingResult(Long paymentId, boolean success) {}
}
