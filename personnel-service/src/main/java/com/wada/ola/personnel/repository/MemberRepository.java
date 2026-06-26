package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMilitaryId(String militaryId);
    Optional<Member> findByEmail(String email);

    // Eagerly fetch command AND its parent two levels deep so neither is ever a bare,
    // unserializable Hibernate proxy when returned as JSON.
    @Override
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.command c LEFT JOIN FETCH c.parent")
    List<Member> findAll();

    @Override
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.command c LEFT JOIN FETCH c.parent WHERE m.id = :id")
    Optional<Member> findById(@Param("id") Long id);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.command c LEFT JOIN FETCH c.parent WHERE m.status = :status")
    List<Member> findByStatus(@Param("status") Member.MemberStatus status);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.command c LEFT JOIN FETCH c.parent WHERE m.rank = :rank")
    List<Member> findByRank(@Param("rank") Member.MilitaryRank rank);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.command c LEFT JOIN FETCH c.parent WHERE m.command.id = :commandId")
    List<Member> findByCommandId(@Param("commandId") Long commandId);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.command c LEFT JOIN FETCH c.parent WHERE m.command.id IN :commandIds")
    List<Member> findByCommandIdIn(@Param("commandIds") List<Long> commandIds);
}
