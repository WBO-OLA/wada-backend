package com.wada.ola.personnel.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.personnel.dto.MemberRankUpdateRequest;
import com.wada.ola.personnel.dto.MemberRequest;
import com.wada.ola.personnel.dto.MemberResponsibilityUpdateRequest;
import com.wada.ola.personnel.dto.MemberRoleUpdateRequest;
import com.wada.ola.personnel.dto.MemberStatusUpdateRequest;
import com.wada.ola.personnel.dto.MemberTransferRequest;
import com.wada.ola.personnel.entity.Command;
import com.wada.ola.personnel.entity.Member;
import com.wada.ola.personnel.entity.MemberRankHistory;
import com.wada.ola.personnel.entity.MemberResponsibilityHistory;
import com.wada.ola.personnel.entity.MemberRoleHistory;
import com.wada.ola.personnel.entity.MemberStatusHistory;
import com.wada.ola.personnel.entity.MemberTransferHistory;
import com.wada.ola.personnel.repository.CommandRepository;
import com.wada.ola.personnel.repository.MemberRankHistoryRepository;
import com.wada.ola.personnel.repository.MemberRepository;
import com.wada.ola.personnel.repository.MemberResponsibilityHistoryRepository;
import com.wada.ola.personnel.repository.MemberRoleHistoryRepository;
import com.wada.ola.personnel.repository.MemberStatusHistoryRepository;
import com.wada.ola.personnel.repository.MemberTransferHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberStatusHistoryRepository statusHistoryRepository;
    private final MemberRankHistoryRepository rankHistoryRepository;
    private final CommandRepository commandRepository;
    private final MemberTransferHistoryRepository transferHistoryRepository;
    private final MemberResponsibilityHistoryRepository responsibilityHistoryRepository;
    private final MemberRoleHistoryRepository roleHistoryRepository;

    public MemberService(MemberRepository memberRepository,
                         MemberStatusHistoryRepository statusHistoryRepository,
                         MemberRankHistoryRepository rankHistoryRepository,
                         CommandRepository commandRepository,
                         MemberTransferHistoryRepository transferHistoryRepository,
                         MemberResponsibilityHistoryRepository responsibilityHistoryRepository,
                         MemberRoleHistoryRepository roleHistoryRepository) {
        this.memberRepository = memberRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.rankHistoryRepository = rankHistoryRepository;
        this.commandRepository = commandRepository;
        this.transferHistoryRepository = transferHistoryRepository;
        this.responsibilityHistoryRepository = responsibilityHistoryRepository;
        this.roleHistoryRepository = roleHistoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
    }

    @Transactional(readOnly = true)
    public List<Member> findByStatus(Member.MemberStatus status) {
        return memberRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Member> findByCommandId(Long commandId) {
        return memberRepository.findByCommandId(commandId);
    }

    @Transactional(readOnly = true)
    public List<Member> findByCommandIds(List<Long> commandIds) {
        if (commandIds == null || commandIds.isEmpty()) return findAll();
        return memberRepository.findByCommandIdIn(commandIds);
    }

    @Transactional(readOnly = true)
    public List<Member> findByRank(Member.MilitaryRank rank) {
        return memberRepository.findByRank(rank);
    }

    @Transactional
    public Member create(MemberRequest request) {
        Member member = new Member();
        applyRequest(member, request);
        return memberRepository.save(member);
    }

    @Transactional
    public Member update(Long id, MemberRequest request) {
        Member member = findById(id);
        applyRequest(member, request);
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateStatus(Long id, MemberStatusUpdateRequest request) {
        Member member = findById(id);
        Member.MemberStatus previousStatus = member.getStatus();

        member.setStatus(request.getStatus());
        memberRepository.save(member);

        MemberStatusHistory history = new MemberStatusHistory();
        history.setMember(member);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(request.getStatus());
        history.setChangedBy(request.getChangedBy());
        history.setNotes(request.getNotes());
        statusHistoryRepository.save(history);

        return member;
    }

    @Transactional
    public Member promoteRank(Long id, MemberRankUpdateRequest request) {
        Member member = findById(id);
        Member.MilitaryRank previousRank = member.getRank();

        member.setRank(request.getRank());
        memberRepository.save(member);

        MemberRankHistory history = new MemberRankHistory();
        history.setMember(member);
        history.setPreviousRank(previousRank);
        history.setNewRank(request.getRank());
        history.setPromotedBy(request.getPromotedBy());
        history.setNotes(request.getNotes());
        rankHistoryRepository.save(history);

        return member;
    }

    @Transactional(readOnly = true)
    public List<MemberStatusHistory> getStatusHistory(Long memberId) {
        findById(memberId);
        return statusHistoryRepository.findByMemberIdOrderByChangedAtDesc(memberId);
    }

    @Transactional(readOnly = true)
    public List<MemberRankHistory> getRankHistory(Long memberId) {
        findById(memberId);
        return rankHistoryRepository.findByMemberIdOrderByPromotedAtDesc(memberId);
    }

    @Transactional
    public Member transfer(Long id, MemberTransferRequest request) {
        Member member = findById(id);
        Command fromCommand = member.getCommand();

        Command toCommand = null;
        if (request.getToCommandId() != null) {
            toCommand = commandRepository.findById(request.getToCommandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Command", request.getToCommandId()));
        }

        member.setCommand(toCommand);
        memberRepository.save(member);

        MemberTransferHistory history = new MemberTransferHistory();
        history.setMember(member);
        history.setFromCommand(fromCommand);
        history.setToCommand(toCommand);
        history.setTransferredBy(request.getTransferredBy());
        history.setReason(request.getReason());
        transferHistoryRepository.save(history);

        return member;
    }

    @Transactional(readOnly = true)
    public List<MemberTransferHistory> getTransferHistory(Long memberId) {
        findById(memberId);
        return transferHistoryRepository.findByMemberIdOrderByTransferredAtDesc(memberId);
    }

    @Transactional
    public Member updateResponsibility(Long id, MemberResponsibilityUpdateRequest request) {
        Member member = findById(id);
        String previous = member.getResponsibility();

        member.setResponsibility(request.getResponsibility());
        memberRepository.save(member);

        MemberResponsibilityHistory history = new MemberResponsibilityHistory();
        history.setMember(member);
        history.setPreviousResponsibility(previous);
        history.setNewResponsibility(request.getResponsibility());
        history.setChangedBy(request.getChangedBy());
        history.setReason(request.getReason());
        responsibilityHistoryRepository.save(history);

        return member;
    }

    @Transactional(readOnly = true)
    public List<MemberResponsibilityHistory> getResponsibilityHistory(Long memberId) {
        findById(memberId);
        return responsibilityHistoryRepository.findByMemberIdOrderByChangedAtDesc(memberId);
    }

    @Transactional
    public Member updateMemberRole(Long id, MemberRoleUpdateRequest request) {
        Member member = findById(id);
        Member.MemberRole previousRole = member.getMemberRole();

        member.setMemberRole(request.getMemberRole());
        memberRepository.save(member);

        MemberRoleHistory history = new MemberRoleHistory();
        history.setMember(member);
        history.setPreviousRole(previousRole);
        history.setNewRole(request.getMemberRole());
        history.setChangedBy(request.getChangedBy());
        history.setReason(request.getReason());
        roleHistoryRepository.save(history);

        return member;
    }

    @Transactional(readOnly = true)
    public List<MemberRoleHistory> getRoleHistory(Long memberId) {
        findById(memberId);
        return roleHistoryRepository.findByMemberIdOrderByChangedAtDesc(memberId);
    }

    @Transactional
    public void delete(Long id) {
        memberRepository.delete(findById(id));
    }

    private void applyRequest(Member member, MemberRequest request) {
        member.setMilitaryId(request.getMilitaryId());
        member.setServiceNumber(request.getServiceNumber());
        member.setCodeName(request.getCodeName());
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setGender(request.getGender());
        member.setNationalId(request.getNationalId());
        member.setNationality(request.getNationality());
        member.setAddress(request.getAddress());
        member.setPhone(request.getPhone());
        member.setEmail(request.getEmail());
        member.setPhotoPath(request.getPhotoPath());
        member.setUnit(request.getUnit());
        member.setRole(request.getRole());
        member.setMemberRole(request.getMemberRole());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setJoinDate(request.getJoinDate());
        member.setRank(request.getRank());
        if (request.getCommandId() != null) {
            Command command = commandRepository.findById(request.getCommandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Command", request.getCommandId()));
            member.setCommand(command);
        } else {
            member.setCommand(null);
        }
        if (request.getStatus() != null) {
            member.setStatus(request.getStatus());
        }
        member.setNotes(request.getNotes());
    }
}
