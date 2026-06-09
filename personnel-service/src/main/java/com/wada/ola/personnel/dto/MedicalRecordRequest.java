package com.wada.ola.personnel.dto;

import java.time.LocalDate;

public class MedicalRecordRequest {
    private LocalDate recordDate;
    private String diagnosis;
    private String treatment;
    private String physician;
    private boolean confidential;
    private String notes;

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
