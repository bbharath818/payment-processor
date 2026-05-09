package com.lloyds.payments.repository;


import com.lloyds.payments.entity.PaymentOutcome;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentOutcomeRepository extends JpaRepository<PaymentOutcome, Long> {
    Page<PaymentOutcome> findByStatus(
            String status,
            Pageable pageable
    );


    List<PaymentOutcome> findByDebitAccountIdOrCreditAccountIdOrderByProcessedAtDesc(
            String debit,
            String credit
    );

    @Query("""
            SELECT COUNT(p),
                   SUM(p.amount)
            FROM PaymentOutcome p
           """)
    Object[] getSummary();
}