package com.wada.ola.reporting.dto;

public class AuditLogEntry {
    private Long id;
    private String username;
    private String action;
    private String targetTable;
    private Long targetId;
    private Long commandId;
    private String createdAt;
    private String sourceService;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getTargetTable() { return targetTable; }
    public void setTargetTable(String targetTable) { this.targetTable = targetTable; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public Long getCommandId() { return commandId; }
    public void setCommandId(Long commandId) { this.commandId = commandId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getSourceService() { return sourceService; }
    public void setSourceService(String sourceService) { this.sourceService = sourceService; }
}
