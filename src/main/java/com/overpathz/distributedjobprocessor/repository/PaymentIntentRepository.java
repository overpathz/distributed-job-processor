package com.overpathz.distributedjobprocessor.repository;

import com.overpathz.distributedjobprocessor.entity.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {

    @Query(value = """
        SELECT * 
        FROM payment_intent
        WHERE status = 'NEW'
        ORDER BY id
        LIMIT :batchSize
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<PaymentIntent> lockBatchForProcessing(@Param("batchSize") int batchSize);

    @Modifying
    @Query("""
        UPDATE PaymentIntent p
        SET p.status = :status
        WHERE p.id IN :ids
        """)
    int updateStatusForIds(@Param("status") String status, @Param("ids") List<Long> ids);
}
