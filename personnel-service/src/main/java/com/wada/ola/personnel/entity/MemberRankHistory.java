package com.wada.ola.personnel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_rank_history")
public class MemberRankHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private Member.MilitaryRank previousRank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Member.MilitaryRank newRank;

    @Column(nullable = false)
    private LocalDateTime promotedAt;

    private String promotedBy;
    private String notes;

    @PrePersist
    protected void onCreate() {
        promotedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Member.MilitaryRank getPreviousRank() { return previousRank; }
    public void setPreviousRank(Member.MilitaryRank previousRank) { this.previousRank = previousRank; }

    public Member.MilitaryRank getNewRank() { return newRank; }
    public void setNewRank(Member.MilitaryRank newRank) { this.newRank = newRank; }

    public LocalDateTime getPromotedAt() { return promotedAt; }

    public String getPromotedBy() { return promotedBy; }
    public void setPromotedBy(String promotedBy) { this.promotedBy = promotedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
