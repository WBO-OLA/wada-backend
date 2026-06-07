package com.wbo.finance.repository;

import com.wbo.finance.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByType(Transaction.TransactionType type);
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    List<Transaction> findByCategory(String category);
}
