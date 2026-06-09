package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.MemberStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberStatusHistoryRepository extends JpaRepository<MemberStatusHistory, Long> {
    List<MemberStatusHistory> findByMemberIdOrderByChangedAtDesc(Long memberId);
}
