package com.wada.ola.reporting.dto;

import java.util.List;

public class AuditLogPage {
    private List<AuditLogEntry> content;
    private long totalElements;
    private int totalPages;
    private int number;
    private int size;

    public AuditLogPage(List<AuditLogEntry> content, long totalElements, int number, int size) {
        this.content = content;
        this.totalElements = totalElements;
        this.number = number;
        this.size = size;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }

    public List<AuditLogEntry> getContent() { return content; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public int getNumber() { return number; }
    public int getSize() { return size; }
}
