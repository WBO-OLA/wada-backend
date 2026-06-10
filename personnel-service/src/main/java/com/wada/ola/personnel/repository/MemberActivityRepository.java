package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.MemberActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberActivityRepository extends JpaRepository<MemberActivity, Long> {
    List<MemberActivity> findByMemberIdOrderByActivityDateDesc(Long memberId);
}
