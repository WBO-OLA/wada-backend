package com.wada.ola.personnel.security;

import com.wada.ola.common.exception.AccessDeniedException;
import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.personnel.entity.Member;
import com.wada.ola.personnel.repository.MemberRepository;
import com.wada.ola.personnel.service.CommandService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Enforces zone-scoped access to members.
 *
 * <p>CHIEF and ADMIN (and unauthenticated internal calls with no role header) are
 * global — they may see and manage every member. Any other role (e.g. a Zone
 * MANAGER) is confined to its own command and all descendant commands: the manager
 * may only read or modify members whose command falls inside that subtree, and may
 * only create/transfer members into commands inside that subtree.
 */
@Component
public class MemberAccessGuard {

    private final MemberRepository memberRepository;
    private final CommandService commandService;

    public MemberAccessGuard(MemberRepository memberRepository, CommandService commandService) {
        this.memberRepository = memberRepository;
        this.commandService = commandService;
    }

    /** True for callers who may act across every zone: CHIEF, ADMIN, or an internal call with no role. */
    public boolean isGlobal(String authRole) {
        return authRole == null || authRole.equals("CHIEF") || authRole.equals("ADMIN");
    }

    /**
     * Guards read/modify of an existing member. Throws {@link AccessDeniedException}
     * (HTTP 403) if a zone-scoped caller targets a member outside their zone.
     */
    public void assertCanAccessMember(String authRole, String authCommand, Long memberId) {
        if (isGlobal(authRole)) return;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", memberId));
        Long memberCommandId = member.getCommand() != null ? member.getCommand().getId() : null;
        assertWithinScope(authCommand, memberCommandId);
    }

    /**
     * Guards the destination command of a create/transfer/update. Throws
     * {@link AccessDeniedException} if a zone-scoped caller tries to place a member
     * into a command outside their zone.
     */
    public void assertCanTargetCommand(String authRole, String authCommand, Long targetCommandId) {
        if (isGlobal(authRole)) return;
        assertWithinScope(authCommand, targetCommandId);
    }

    /**
     * The command IDs a zone-scoped caller may see: their own command plus every
     * descendant (region, unit, …). Throws {@link AccessDeniedException} if the
     * caller has no zone assigned. Callers must first check {@link #isGlobal} —
     * a global caller is not confined to any subtree.
     */
    public List<Long> scopedCommandIds(String authCommand) {
        if (authCommand == null || authCommand.isBlank()) {
            throw new AccessDeniedException("Your account is not assigned to a zone, so you cannot view personnel data.");
        }
        return commandService.getAllDescendantIds(Long.parseLong(authCommand));
    }

    private void assertWithinScope(String authCommand, Long commandId) {
        List<Long> scope = scopedCommandIds(authCommand);
        if (commandId == null || !scope.contains(commandId)) {
            throw new AccessDeniedException("You are not authorized to manage members outside your zone.");
        }
    }
}
