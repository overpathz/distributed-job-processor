package com.overpathz.distributedjobprocessor.service;

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
    public void updateBatchStatus(List<Long> processedIds, List<Long> failedIds) {
        if (!processedIds.isEmpty()) {
            paymentIntentRepository.updateStatusForIds("PROCESSED", processedIds);
        }
        if (!failedIds.isEmpty()) {
            paymentIntentRepository.updateStatusForIds("FAILED", failedIds);
        }
    }
}
