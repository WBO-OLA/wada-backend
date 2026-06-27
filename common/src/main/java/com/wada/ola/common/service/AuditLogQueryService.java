package com.wada.ola.common.service;

import com.wada.ola.common.entity.AuditLog;
import com.wada.ola.common.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogQueryService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogQueryService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    private static final LocalDateTime EARLIEST = LocalDateTime.of(1970, 1, 1, 0, 0);
    private static final LocalDateTime LATEST = LocalDateTime.of(2999, 12, 31, 23, 59, 59);

    public Page<AuditLog> search(String action, String targetTable, Long commandId,
                                  LocalDateTime from, LocalDateTime to,
                                  int page, int size) {
        // Postgres' JDBC driver can't infer a type for a bind parameter whose value is null,
        // so sentinel bounds are used instead of passing null timestamps into the query.
        return auditLogRepository.search(
                blankToNull(action), blankToNull(targetTable), commandId,
                from != null ? from : EARLIEST,
                to != null ? to : LATEST,
                PageRequest.of(Math.max(page, 0), size > 0 ? size : 20));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
