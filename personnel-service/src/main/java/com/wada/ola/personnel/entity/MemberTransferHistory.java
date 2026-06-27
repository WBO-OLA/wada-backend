package com.wada.ola.personnel.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_transfer_history")
public class MemberTransferHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @JsonIgnoreProperties("parent")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_command_id")
    private Command fromCommand;

    @JsonIgnoreProperties("parent")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_command_id")
    private Command toCommand;

    @Column(nullable = false)
    private LocalDateTime transferredAt;

    private String transferredBy;
    private String reason;

    @PrePersist
    protected void onCreate() {
        transferredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Command getFromCommand() { return fromCommand; }
    public void setFromCommand(Command fromCommand) { this.fromCommand = fromCommand; }

    public Command getToCommand() { return toCommand; }
    public void setToCommand(Command toCommand) { this.toCommand = toCommand; }

    public LocalDateTime getTransferredAt() { return transferredAt; }

    public String getTransferredBy() { return transferredBy; }
    public void setTransferredBy(String transferredBy) { this.transferredBy = transferredBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
