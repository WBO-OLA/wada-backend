package com.wada.ola.inventory.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.inventory.dto.StockTransactionRequest;
import com.wada.ola.inventory.entity.StockTransaction;
import com.wada.ola.inventory.service.StockTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/stock")
public class StockTransactionController {

    private final StockTransactionService stockTransactionService;

    public StockTransactionController(StockTransactionService stockTransactionService) {
        this.stockTransactionService = stockTransactionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockTransaction>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(stockTransactionService.findAll()));
    }

    @PostMapping("/in")
    @Audited(action = "STOCK_IN", targetTable = "stock_transactions")
    public ResponseEntity<ApiResponse<StockTransaction>> stockIn(@RequestBody StockTransactionRequest request) {
        request.setType(StockTransaction.TransactionType.IN);
        return ResponseEntity.ok(ApiResponse.ok("Stock received", stockTransactionService.process(request)));
    }

    @PostMapping("/out")
    @Audited(action = "STOCK_OUT", targetTable = "stock_transactions")
    public ResponseEntity<ApiResponse<StockTransaction>> stockOut(@RequestBody StockTransactionRequest request) {
        request.setType(StockTransaction.TransactionType.OUT);
        return ResponseEntity.ok(ApiResponse.ok("Stock issued", stockTransactionService.process(request)));
    }

    @PostMapping("/adjustment")
    @Audited(action = "STOCK_ADJUSTMENT", targetTable = "stock_transactions")
    public ResponseEntity<ApiResponse<StockTransaction>> adjust(@RequestBody StockTransactionRequest request) {
        request.setType(StockTransaction.TransactionType.ADJUSTMENT);
        return ResponseEntity.ok(ApiResponse.ok("Stock adjusted", stockTransactionService.process(request)));
    }

    @GetMapping("/item/{itemId}/history")
    public ResponseEntity<ApiResponse<List<StockTransaction>>> getItemHistory(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.ok(stockTransactionService.getHistoryByItem(itemId)));
    }

    @GetMapping("/warehouse/{warehouseId}/history")
    public ResponseEntity<ApiResponse<List<StockTransaction>>> getWarehouseHistory(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(ApiResponse.ok(stockTransactionService.getHistoryByWarehouse(warehouseId)));
    }
}
