package com.wada.ola.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeRequest {
    private String title;
    private BigDecimal amount;
    private String source;
    private String category;
    private LocalDate receivedDate;
    private String reference;
    private String recordedBy;
    private String notes;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
