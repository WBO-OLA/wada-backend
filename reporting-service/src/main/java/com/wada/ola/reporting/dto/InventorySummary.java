package com.wada.ola.reporting.dto;

import java.math.BigDecimal;
import java.util.Map;

public class InventorySummary {
    private long totalItems;
    private long totalWarehouses;
    private long activeWarehouses;
    private long lowStockCount;
    private long outOfStockCount;
    private BigDecimal totalStockValue;
    private Map<String, Long> byCategory;

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long v) { this.totalItems = v; }
    public long getTotalWarehouses() { return totalWarehouses; }
    public void setTotalWarehouses(long v) { this.totalWarehouses = v; }
    public long getActiveWarehouses() { return activeWarehouses; }
    public void setActiveWarehouses(long v) { this.activeWarehouses = v; }
    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long v) { this.lowStockCount = v; }
    public long getOutOfStockCount() { return outOfStockCount; }
    public void setOutOfStockCount(long v) { this.outOfStockCount = v; }
    public BigDecimal getTotalStockValue() { return totalStockValue; }
    public void setTotalStockValue(BigDecimal v) { this.totalStockValue = v; }
    public Map<String, Long> getByCategory() { return byCategory; }
    public void setByCategory(Map<String, Long> v) { this.byCategory = v; }
}
