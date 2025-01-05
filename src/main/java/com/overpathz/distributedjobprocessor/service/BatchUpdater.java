package com.overpathz.distributedjobprocessor.service;

import com.overpathz.distributedjobprocessor.entity.PaymentIntent;
import com.overpathz.distributedjobprocessor.repository.PaymentIntentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BatchUpdater {
    private final PaymentIntentRepository paymentIntentRepository;

    public BatchUpdater(PaymentIntentRepository paymentIntentRepository) {
        this.paymentIntentRepository = paymentIntentRepository;
    }

    @Transactional
    public void updateBatchStatus(List<PaymentIntent> batch) {
        List<Long> processedIds = batch.stream()
                .filter(i -> "PROCESSED".equals(i.getStatus()))
                .map(PaymentIntent::getId)
                .toList();

        List<Long> failedIds = batch.stream()
                .filter(i -> "FAILED".equals(i.getStatus()))
                .map(PaymentIntent::getId)
                .toList();

        if (!processedIds.isEmpty()) {
            paymentIntentRepository.updateStatusForIds("PROCESSED", processedIds);
        }
        if (!failedIds.isEmpty()) {
            paymentIntentRepository.updateStatusForIds("FAILED", failedIds);
        }
    }
}
