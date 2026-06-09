package com.wada.ola.personnel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_status_history")
public class MemberStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private Member.MemberStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Member.MemberStatus newStatus;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    private String changedBy;
    private String notes;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Member.MemberStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(Member.MemberStatus previousStatus) { this.previousStatus = previousStatus; }

    public Member.MemberStatus getNewStatus() { return newStatus; }
    public void setNewStatus(Member.MemberStatus newStatus) { this.newStatus = newStatus; }

    public LocalDateTime getChangedAt() { return changedAt; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
