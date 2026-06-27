package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.MemberTransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberTransferHistoryRepository extends JpaRepository<MemberTransferHistory, Long> {

    @Query("SELECT t FROM MemberTransferHistory t " +
           "LEFT JOIN FETCH t.fromCommand fc LEFT JOIN FETCH fc.parent " +
           "LEFT JOIN FETCH t.toCommand tc LEFT JOIN FETCH tc.parent " +
           "WHERE t.member.id = :memberId ORDER BY t.transferredAt DESC")
    List<MemberTransferHistory> findByMemberIdOrderByTransferredAtDesc(@Param("memberId") Long memberId);
}
