package com.wada.ola.personnel.dto;

import com.wada.ola.personnel.entity.Member;

public class MemberRankUpdateRequest {

    private Member.MilitaryRank rank;
    private String promotedBy;
    private String notes;

    public Member.MilitaryRank getRank() { return rank; }
    public void setRank(Member.MilitaryRank rank) { this.rank = rank; }

    public String getPromotedBy() { return promotedBy; }
    public void setPromotedBy(String promotedBy) { this.promotedBy = promotedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
