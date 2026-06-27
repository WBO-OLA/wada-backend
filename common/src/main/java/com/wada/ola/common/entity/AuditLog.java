package com.wada.ola.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String action;

    @Column(name = "target_table", nullable = false)
    private String targetTable;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "command_id")
    private Long commandId;

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
}
