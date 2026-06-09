package com.wada.ola.personnel.dto;

import com.wada.ola.personnel.entity.Member;

public class MemberStatusUpdateRequest {

    private Member.MemberStatus status;
    private String changedBy;
    private String notes;

    public Member.MemberStatus getStatus() { return status; }
    public void setStatus(Member.MemberStatus status) { this.status = status; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
