package com.wada.ola.common.aspect;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.common.entity.AuditLog;
import com.wada.ola.common.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditLogRepository auditLogRepository;

    public AuditAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @AfterReturning(pointcut = "@annotation(audited)", returning = "result")
    public void recordAudit(JoinPoint joinPoint, Audited audited, Object result) {
        try {
            AuditLog entry = new AuditLog();
            entry.setUsername(resolveUsername());
            entry.setAction(audited.action());
            entry.setTargetTable(audited.targetTable());
            entry.setTargetId(resolveTargetId(joinPoint, result));
            entry.setCommandId(resolveCommandId());
            auditLogRepository.save(entry);
        } catch (Exception e) {
            // Audit logging must never break the actual business operation.
            log.warn("Failed to record audit log entry: {}", e.getMessage());
        }
    }

    private String resolveUsername() {
        HttpServletRequest request = currentRequest();
        if (request == null) return "unknown";
        String user = request.getHeader("X-Auth-User");
        return user != null ? user : "unknown";
    }

    private Long resolveCommandId() {
        HttpServletRequest request = currentRequest();
        if (request == null) return null;
        String header = request.getHeader("X-Command-Id");
        if (header == null || header.isBlank()) return null;
        try { return Long.parseLong(header.trim()); } catch (NumberFormatException e) { return null; }
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private Long resolveTargetId(JoinPoint joinPoint, Object result) {
        // Prefer the response body's own id (correct for create/update, where a path
        // variable like memberId would otherwise be mistaken for the affected record).
        Long fromResult = findIdInResult(result);
        if (fromResult != null) return fromResult;
        return findIdInArgs(joinPoint);
    }

    private Long findIdInArgs(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        if (paramNames == null) return null;
        for (int i = 0; i < paramNames.length; i++) {
            if ("id".equals(paramNames[i]) && args[i] instanceof Long) {
                return (Long) args[i];
            }
        }
        // No exact "id" param (e.g. delete(docId)/delete(activityId)) — if it's the only
        // Long argument, it's unambiguously the target of a single-id write endpoint.
        Long onlyLongArg = null;
        int longArgCount = 0;
        for (Object arg : args) {
            if (arg instanceof Long) {
                longArgCount++;
                onlyLongArg = (Long) arg;
            }
        }
        return longArgCount == 1 ? onlyLongArg : null;
    }

    private Long findIdInResult(Object result) {
        try {
            Object data = result;
            if (result instanceof ResponseEntity<?> responseEntity) {
                data = responseEntity.getBody();
            }
            if (data instanceof ApiResponse<?> apiResponse) {
                data = apiResponse.getData();
            }
            if (data == null) return null;
            Object id = data.getClass().getMethod("getId").invoke(data);
            return id instanceof Long ? (Long) id : null;
        } catch (Exception e) {
            return null;
        }
    }
}
