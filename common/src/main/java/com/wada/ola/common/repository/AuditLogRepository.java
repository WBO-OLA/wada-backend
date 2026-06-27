package com.wada.ola.common.repository;

import com.wada.ola.common.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:action IS NULL OR a.action = :action) AND " +
            "(:targetTable IS NULL OR a.targetTable = :targetTable) AND " +
            "a.createdAt >= :from AND a.createdAt <= :to " +
            "ORDER BY a.createdAt DESC")
    Page<AuditLog> search(@Param("action") String action,
                           @Param("targetTable") String targetTable,
                           @Param("from") LocalDateTime from,
                           @Param("to") LocalDateTime to,
                           Pageable pageable);
}
