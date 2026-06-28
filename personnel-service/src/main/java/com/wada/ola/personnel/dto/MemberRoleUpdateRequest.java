package com.wada.ola.personnel.dto;

import com.wada.ola.personnel.entity.Member;

public class MemberRoleUpdateRequest {

    private Member.MemberRole memberRole;
    private String changedBy;
    private String reason;

    public Member.MemberRole getMemberRole() { return memberRole; }
    public void setMemberRole(Member.MemberRole memberRole) { this.memberRole = memberRole; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
