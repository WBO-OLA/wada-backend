package com.wada.ola.inventory.repository;

import com.wada.ola.inventory.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByItemIdOrderByCreatedAtDesc(Long itemId);
    List<StockTransaction> findByWarehouseIdOrderByCreatedAtDesc(Long warehouseId);
    List<StockTransaction> findByType(StockTransaction.TransactionType type);
}
