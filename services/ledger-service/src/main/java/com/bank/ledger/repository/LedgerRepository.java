package com.bank.ledger.repository;

import com.bank.ledger.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByAccount(String account);

    List<LedgerEntry> findByTransactionId(String transactionId);

    List<LedgerEntry> findByAccountAndTimestampBetween(
            String account,
            Instant start,
            Instant end
    );

    long countByAccount(String account);
}