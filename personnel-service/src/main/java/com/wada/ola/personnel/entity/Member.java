package com.wada.ola.personnel.entity;

import com.wada.ola.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "members")
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String militaryId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String nationalId;

    private String phone;
    private String email;

    private LocalDate dateOfBirth;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MilitaryRank rank = MilitaryRank.RECRUIT;

    @Column(nullable = false)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    private String notes;

    public enum MilitaryRank {
        RECRUIT, PRIVATE, CORPORAL, SERGEANT, STAFF_SERGEANT,
        WARRANT_OFFICER, SECOND_LIEUTENANT, FIRST_LIEUTENANT,
        CAPTAIN, MAJOR, LIEUTENANT_COLONEL, COLONEL,
        BRIGADIER_GENERAL, MAJOR_GENERAL, LIEUTENANT_GENERAL, GENERAL
    }

    public enum MemberStatus {
        ACTIVE, INJURED, RETIRED, PASSED_AWAY
    }

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

    public MilitaryRank getRank() { return rank; }
    public void setRank(MilitaryRank rank) { this.rank = rank; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public MemberStatus getStatus() { return status; }
    public void setStatus(MemberStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
