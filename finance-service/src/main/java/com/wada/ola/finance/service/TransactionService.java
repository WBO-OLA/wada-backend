package com.wada.ola.finance.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.finance.entity.Transaction;
import com.wada.ola.finance.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }

    public Transaction create(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateStatus(Long id, Transaction.TransactionStatus status) {
        Transaction transaction = findById(id);
        transaction.setStatus(status);
        return transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        transactionRepository.delete(findById(id));
    }
}

