package com.wada.ola.reporting.dto;

import java.time.LocalDateTime;

public class DashboardReport {

    private PersonnelSummary personnel;
    private InventorySummary inventory;
    private FinanceSummary finance;
    private LocalDateTime generatedAt;

    public DashboardReport() {}

    public DashboardReport(PersonnelSummary personnel, InventorySummary inventory,
                            FinanceSummary finance) {
        this.personnel = personnel;
        this.inventory = inventory;
        this.finance = finance;
        this.generatedAt = LocalDateTime.now();
    }

    public PersonnelSummary getPersonnel() { return personnel; }
    public void setPersonnel(PersonnelSummary personnel) { this.personnel = personnel; }

    public InventorySummary getInventory() { return inventory; }
    public void setInventory(InventorySummary inventory) { this.inventory = inventory; }

    public FinanceSummary getFinance() { return finance; }
    public void setFinance(FinanceSummary finance) { this.finance = finance; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
