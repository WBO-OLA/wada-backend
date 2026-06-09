package com.wada.ola.inventory.dto;

import java.math.BigDecimal;
import java.util.Map;

public class InventorySummaryDTO {

    private long totalItems;
    private long totalWarehouses;
    private long activeWarehouses;
    private long lowStockCount;
    private long outOfStockCount;
    private BigDecimal totalStockValue;
    private Map<String, Long> byCategory;

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public long getTotalWarehouses() { return totalWarehouses; }
    public void setTotalWarehouses(long totalWarehouses) { this.totalWarehouses = totalWarehouses; }

    public long getActiveWarehouses() { return activeWarehouses; }
    public void setActiveWarehouses(long activeWarehouses) { this.activeWarehouses = activeWarehouses; }

    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }

    public long getOutOfStockCount() { return outOfStockCount; }
    public void setOutOfStockCount(long outOfStockCount) { this.outOfStockCount = outOfStockCount; }

    public BigDecimal getTotalStockValue() { return totalStockValue; }
    public void setTotalStockValue(BigDecimal totalStockValue) { this.totalStockValue = totalStockValue; }

    public Map<String, Long> getByCategory() { return byCategory; }
    public void setByCategory(Map<String, Long> byCategory) { this.byCategory = byCategory; }
}
