package com.wada.ola.personnel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_responsibility_history")
public class MemberResponsibilityHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String previousResponsibility;

    @Column(nullable = false)
    private String newResponsibility;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    private String changedBy;
    private String reason;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public String getPreviousResponsibility() { return previousResponsibility; }
    public void setPreviousResponsibility(String previousResponsibility) { this.previousResponsibility = previousResponsibility; }

    public String getNewResponsibility() { return newResponsibility; }
    public void setNewResponsibility(String newResponsibility) { this.newResponsibility = newResponsibility; }

    public LocalDateTime getChangedAt() { return changedAt; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
