package com.wada.ola.personnel.dto;

import com.wada.ola.personnel.entity.Member;
import java.time.LocalDate;

public class MemberRequest {

    private String militaryId;
    private String firstName;
    private String lastName;
    private String nationalId;
    private String phone;
    private String email;
    private LocalDate dateOfBirth;
    private LocalDate joinDate;
    private Member.MilitaryRank rank;
    private String unit;
    private Member.MemberStatus status;
    private String notes;

    public String getMilitaryId() { return militaryId; }
    public void setMilitaryId(String militaryId) { this.militaryId = militaryId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public Member.MilitaryRank getRank() { return rank; }
    public void setRank(Member.MilitaryRank rank) { this.rank = rank; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Member.MemberStatus getStatus() { return status; }
    public void setStatus(Member.MemberStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
