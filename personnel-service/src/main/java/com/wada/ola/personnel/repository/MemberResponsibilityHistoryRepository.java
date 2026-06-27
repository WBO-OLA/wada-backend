package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.MemberResponsibilityHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberResponsibilityHistoryRepository extends JpaRepository<MemberResponsibilityHistory, Long> {
    List<MemberResponsibilityHistory> findByMemberIdOrderByChangedAtDesc(Long memberId);
}
