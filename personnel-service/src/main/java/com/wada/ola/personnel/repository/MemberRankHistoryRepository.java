package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.MemberRankHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRankHistoryRepository extends JpaRepository<MemberRankHistory, Long> {
    List<MemberRankHistory> findByMemberIdOrderByPromotedAtDesc(Long memberId);
}
