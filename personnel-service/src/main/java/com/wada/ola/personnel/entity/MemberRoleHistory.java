package com.wada.ola.personnel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_role_history")
public class MemberRoleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private Member.MemberRole previousRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Member.MemberRole newRole;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    private String changedBy;
    private String reason;

    @PrePersist
    protected void onCreate() { changedAt = LocalDateTime.now(); }

    public Long getId() { return id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Member.MemberRole getPreviousRole() { return previousRole; }
    public void setPreviousRole(Member.MemberRole previousRole) { this.previousRole = previousRole; }

    public Member.MemberRole getNewRole() { return newRole; }
    public void setNewRole(Member.MemberRole newRole) { this.newRole = newRole; }

    public LocalDateTime getChangedAt() { return changedAt; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
