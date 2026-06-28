package com.wada.ola.personnel.dto;

import com.wada.ola.personnel.entity.Member;
import java.time.LocalDate;

public class MemberRequest {

    private String militaryId;
    private String serviceNumber;
    private String codeName;
    private String firstName;
    private String lastName;
    private Member.Gender gender;
    private String nationalId;
    private String nationality;
    private String address;
    private String phone;
    private String email;
    private String photoPath;
    private String unit;
    private String role;
    private Member.MemberRole memberRole;
    private LocalDate dateOfBirth;
    private LocalDate joinDate;
    private Member.MilitaryRank rank;
    private Long commandId;
    private Member.MemberStatus status;
    private String notes;

    public String getMilitaryId() { return militaryId; }
    public void setMilitaryId(String militaryId) { this.militaryId = militaryId; }

    public String getServiceNumber() { return serviceNumber; }
    public void setServiceNumber(String serviceNumber) { this.serviceNumber = serviceNumber; }

    public String getCodeName() { return codeName; }
    public void setCodeName(String codeName) { this.codeName = codeName; }

    public Member.Gender getGender() { return gender; }
    public void setGender(Member.Gender gender) { this.gender = gender; }

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

    public Member.MemberRole getMemberRole() { return memberRole; }
    public void setMemberRole(Member.MemberRole memberRole) { this.memberRole = memberRole; }

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

    public Long getCommandId() { return commandId; }
    public void setCommandId(Long commandId) { this.commandId = commandId; }

    public Member.MemberStatus getStatus() { return status; }
    public void setStatus(Member.MemberStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
