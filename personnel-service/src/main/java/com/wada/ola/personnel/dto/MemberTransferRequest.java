package com.wada.ola.personnel.dto;

public class MemberTransferRequest {

    private Long toCommandId;
    private String transferredBy;
    private String reason;

    public Long getToCommandId() { return toCommandId; }
    public void setToCommandId(Long toCommandId) { this.toCommandId = toCommandId; }

    public String getTransferredBy() { return transferredBy; }
    public void setTransferredBy(String transferredBy) { this.transferredBy = transferredBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
