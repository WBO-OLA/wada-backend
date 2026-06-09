package com.wada.ola.inventory.service;

import com.wada.ola.inventory.dto.StockTransactionRequest;
import com.wada.ola.inventory.entity.Item;
import com.wada.ola.inventory.entity.StockTransaction;
import com.wada.ola.inventory.entity.Warehouse;
import com.wada.ola.inventory.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockTransactionService {

    private final StockTransactionRepository transactionRepository;
    private final ItemService itemService;
    private final WarehouseService warehouseService;

    public StockTransactionService(StockTransactionRepository transactionRepository,
                                   ItemService itemService,
                                   WarehouseService warehouseService) {
        this.transactionRepository = transactionRepository;
        this.itemService = itemService;
        this.warehouseService = warehouseService;
    }

    @Transactional
    public StockTransaction process(StockTransactionRequest request) {
        Item item = itemService.findById(request.getItemId());
        Warehouse warehouse = warehouseService.findById(request.getWarehouseId());

        switch (request.getType()) {
            case IN -> item.setQuantity(item.getQuantity() + request.getQuantity());
            case OUT -> {
                if (item.getQuantity() < request.getQuantity()) {
                    throw new IllegalArgumentException(
                            "Insufficient stock: available=" + item.getQuantity()
                                    + ", requested=" + request.getQuantity());
                }
                item.setQuantity(item.getQuantity() - request.getQuantity());
            }
            case ADJUSTMENT -> item.setQuantity(request.getQuantity());
        }
        itemService.save(item);

        StockTransaction tx = new StockTransaction();
        tx.setItem(item);
        tx.setWarehouse(warehouse);
        tx.setType(request.getType());
        tx.setQuantity(request.getQuantity());
        tx.setReferenceNumber(request.getReferenceNumber());
        tx.setPerformedBy(request.getPerformedBy());
        tx.setNotes(request.getNotes());
        return transactionRepository.save(tx);
    }

    public List<StockTransaction> getHistoryByItem(Long itemId) {
        itemService.findById(itemId);
        return transactionRepository.findByItemIdOrderByCreatedAtDesc(itemId);
    }

    public List<StockTransaction> getHistoryByWarehouse(Long warehouseId) {
        warehouseService.findById(warehouseId);
        return transactionRepository.findByWarehouseIdOrderByCreatedAtDesc(warehouseId);
    }

    public List<StockTransaction> findAll() {
        return transactionRepository.findAll();
    }
}
