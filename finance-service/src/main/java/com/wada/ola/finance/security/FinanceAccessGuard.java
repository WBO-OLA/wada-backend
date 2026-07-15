package com.wada.ola.finance.security;

import com.wada.ola.common.exception.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * Enforces zone-scoped access to finance records (incomes, expenses, budgets).
 *
 * <p>CHIEF and ADMIN (and internal calls with no role header) are global — they may
 * see and manage every command's finances. Any other role (e.g. a Zone MANAGER) is
 * confined to their own command.
 *
 * <p>Unlike the personnel guard, this one matches the caller's command <em>exactly</em>
 * rather than by subtree: finance-service does not own the command hierarchy (that lives
 * in personnel-service), so it cannot resolve descendant commands. This mirrors the
 * exact-match scoping the finance list endpoints already used.
 */
@Component
public class FinanceAccessGuard {

    /** True for callers who may act across every command: CHIEF, ADMIN, or an internal call with no role. */
    public boolean isGlobal(String authRole) {
        return authRole == null || authRole.equals("CHIEF") || authRole.equals("ADMIN");
    }

    /**
     * Resolves the command a list query should be scoped to: global callers get whatever
     * they requested; a zone-scoped caller is pinned to their own command.
     */
    public Long resolveScopedCommandId(String authRole, String authCommand, Long requested) {
        if (isGlobal(authRole)) return requested;
        if (authCommand == null || authCommand.isBlank()) {
            throw new AccessDeniedException("Your account is not assigned to a zone, so you cannot view finance records.");
        }
        return Long.parseLong(authCommand);
    }

    /**
     * Guards read/modify of a specific record. Throws {@link AccessDeniedException}
     * (HTTP 403) if a zone-scoped caller targets a record owned by another command.
     * Also used to guard the target command of a create.
     */
    public void assertCommandInScope(String authRole, String authCommand, Long recordCommandId) {
        if (isGlobal(authRole)) return;
        if (authCommand == null || authCommand.isBlank()) {
            throw new AccessDeniedException("Your account is not assigned to a zone, so you cannot manage finance records.");
        }
        if (recordCommandId == null || !recordCommandId.equals(Long.parseLong(authCommand))) {
            throw new AccessDeniedException("You are not authorized to manage finance records outside your zone.");
        }
    }
}
