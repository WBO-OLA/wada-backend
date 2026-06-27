package com.wada.ola.personnel.dto;

public class MemberResponsibilityUpdateRequest {

    private String responsibility;
    private String changedBy;
    private String reason;

    public String getResponsibility() { return responsibility; }
    public void setResponsibility(String responsibility) { this.responsibility = responsibility; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
