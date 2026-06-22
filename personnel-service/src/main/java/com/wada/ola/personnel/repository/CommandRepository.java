package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommandRepository extends JpaRepository<Command, Long> {

    // Eagerly fetch parent so it's never a bare, unserializable Hibernate proxy when returned as JSON.
    @Override
    @Query("SELECT c FROM Command c LEFT JOIN FETCH c.parent")
    List<Command> findAll();

    @Override
    @Query("SELECT c FROM Command c LEFT JOIN FETCH c.parent WHERE c.id = :id")
    Optional<Command> findById(@Param("id") Long id);

    @Query("SELECT c FROM Command c LEFT JOIN FETCH c.parent WHERE c.parent.id = :parentId")
    List<Command> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Command c LEFT JOIN FETCH c.parent WHERE c.parent IS NULL")
    List<Command> findByParentIsNull();
}
