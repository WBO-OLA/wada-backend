package com.wada.ola.personnel.entity;

import com.wada.ola.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "members")
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String militaryId;

    private String serviceNumber;
    private String codeName;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true)
    private String nationalId;

    private String nationality;
    private String address;
    private String phone;
    private String email;
    private String photoPath;
    private String unit;
    private String role;
    private String responsibility;

    private LocalDate dateOfBirth;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MilitaryRank rank = MilitaryRank.RECRUIT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "command_id")
    private Command command;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    private String notes;

    public enum Gender {
        MALE, FEMALE, OTHER
    }

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

    public String getServiceNumber() { return serviceNumber; }
    public void setServiceNumber(String serviceNumber) { this.serviceNumber = serviceNumber; }

    public String getCodeName() { return codeName; }
    public void setCodeName(String codeName) { this.codeName = codeName; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getResponsibility() { return responsibility; }
    public void setResponsibility(String responsibility) { this.responsibility = responsibility; }

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

    public Command getCommand() { return command; }
    public void setCommand(Command command) { this.command = command; }

    public MemberStatus getStatus() { return status; }
    public void setStatus(MemberStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
