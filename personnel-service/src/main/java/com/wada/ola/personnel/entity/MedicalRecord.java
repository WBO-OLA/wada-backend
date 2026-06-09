package com.wada.ola.personnel.entity;

import com.wada.ola.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medical_records")
public class MedicalRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(nullable = false)
    private String diagnosis;

    private String treatment;
    private String physician;
    private boolean confidential = false;
    private String notes;

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getPhysician() { return physician; }
    public void setPhysician(String physician) { this.physician = physician; }

    public boolean isConfidential() { return confidential; }
    public void setConfidential(boolean confidential) { this.confidential = confidential; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
