package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.MemberRoleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberRoleHistoryRepository extends JpaRepository<MemberRoleHistory, Long> {
    List<MemberRoleHistory> findByMemberIdOrderByChangedAtDesc(Long memberId);
}
