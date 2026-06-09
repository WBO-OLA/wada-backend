package com.wada.ola.inventory.dto;

import com.wada.ola.inventory.entity.StockTransaction;

public class StockTransactionRequest {

    private Long itemId;
    private Long warehouseId;
    private StockTransaction.TransactionType type;
    private Integer quantity;
    private String referenceNumber;
    private String performedBy;
    private String notes;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public StockTransaction.TransactionType getType() { return type; }
    public void setType(StockTransaction.TransactionType type) { this.type = type; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
