package com.wada.ola.finance.dto;

public class BudgetActivationRequest {
    private String approvedBy;
    private String notes;

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
