package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMilitaryId(String militaryId);
    Optional<Member> findByEmail(String email);
    List<Member> findByStatus(Member.MemberStatus status);
    List<Member> findByRank(Member.MilitaryRank rank);
    List<Member> findByUnit(String unit);
    List<Member> findByUnitAndStatus(String unit, Member.MemberStatus status);
}
