package com.wada.ola.personnel.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.personnel.dto.MemberRankUpdateRequest;
import com.wada.ola.personnel.dto.MemberRequest;
import com.wada.ola.personnel.dto.MemberStatusUpdateRequest;
import com.wada.ola.personnel.entity.Member;
import com.wada.ola.personnel.entity.MemberRankHistory;
import com.wada.ola.personnel.entity.MemberStatusHistory;
import com.wada.ola.personnel.repository.MemberRankHistoryRepository;
import com.wada.ola.personnel.repository.MemberRepository;
import com.wada.ola.personnel.repository.MemberStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberStatusHistoryRepository statusHistoryRepository;
    private final MemberRankHistoryRepository rankHistoryRepository;

    public MemberService(MemberRepository memberRepository,
                         MemberStatusHistoryRepository statusHistoryRepository,
                         MemberRankHistoryRepository rankHistoryRepository) {
        this.memberRepository = memberRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.rankHistoryRepository = rankHistoryRepository;
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
    }

    public List<Member> findByStatus(Member.MemberStatus status) {
        return memberRepository.findByStatus(status);
    }

    public List<Member> findByUnit(String unit) {
        return memberRepository.findByUnit(unit);
    }

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

    public List<MemberStatusHistory> getStatusHistory(Long memberId) {
        findById(memberId);
        return statusHistoryRepository.findByMemberIdOrderByChangedAtDesc(memberId);
    }

    public List<MemberRankHistory> getRankHistory(Long memberId) {
        findById(memberId);
        return rankHistoryRepository.findByMemberIdOrderByPromotedAtDesc(memberId);
    }

    public void delete(Long id) {
        memberRepository.delete(findById(id));
    }

    private void applyRequest(Member member, MemberRequest request) {
        member.setMilitaryId(request.getMilitaryId());
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setNationalId(request.getNationalId());
        member.setPhone(request.getPhone());
        member.setEmail(request.getEmail());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setJoinDate(request.getJoinDate());
        member.setRank(request.getRank());
        member.setUnit(request.getUnit());
        if (request.getStatus() != null) {
            member.setStatus(request.getStatus());
        }
        member.setNotes(request.getNotes());
    }
}
