package com.wada.ola.personnel.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.personnel.dto.LifetimeSummaryDTO;
import com.wada.ola.personnel.dto.MemberRankUpdateRequest;
import com.wada.ola.personnel.dto.MemberRequest;
import com.wada.ola.personnel.dto.MemberResponsibilityUpdateRequest;
import com.wada.ola.personnel.dto.MemberRoleUpdateRequest;
import com.wada.ola.personnel.dto.MemberStatusUpdateRequest;
import com.wada.ola.personnel.dto.MemberTransferRequest;
import com.wada.ola.personnel.entity.Member;
import com.wada.ola.personnel.entity.MemberRankHistory;
import com.wada.ola.personnel.entity.MemberResponsibilityHistory;
import com.wada.ola.personnel.entity.MemberRoleHistory;
import com.wada.ola.personnel.entity.MemberStatusHistory;
import com.wada.ola.personnel.entity.MemberTransferHistory;
import com.wada.ola.personnel.security.MemberAccessGuard;
import com.wada.ola.personnel.service.MemberLifetimeService;
import com.wada.ola.personnel.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/personnel/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberLifetimeService lifetimeService;
    private final MemberAccessGuard accessGuard;

    public MemberController(MemberService memberService,
                            MemberLifetimeService lifetimeService, MemberAccessGuard accessGuard) {
        this.memberService = memberService;
        this.lifetimeService = lifetimeService;
        this.accessGuard = accessGuard;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Member>>> getAll(
            @RequestParam(required = false) Member.MemberStatus status,
            @RequestParam(required = false) Long commandId,
            @RequestParam(required = false) Member.MilitaryRank rank,
            @RequestParam(required = false) String commandIds,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {

        if (!accessGuard.isGlobal(authRole)) {
            // Zone-scoped caller: confined to their own command subtree. A caller with no
            // zone assigned is denied (scopedCommandIds throws) rather than shown everything.
            List<Long> scopedIds = accessGuard.scopedCommandIds(authCommand);
            List<Member> scoped = memberService.findByCommandIds(scopedIds);
            if (status != null) {
                final Member.MemberStatus s = status;
                scoped = scoped.stream().filter(m -> m.getStatus() == s).collect(Collectors.toList());
            }
            if (rank != null) {
                final Member.MilitaryRank r = rank;
                scoped = scoped.stream().filter(m -> m.getRank() == r).collect(Collectors.toList());
            }
            return ResponseEntity.ok(ApiResponse.ok(scoped));
        }

        List<Member> members;
        if (commandIds != null && !commandIds.isBlank()) {
            List<Long> ids = Arrays.stream(commandIds.split(","))
                    .map(String::trim).map(Long::parseLong).collect(Collectors.toList());
            members = memberService.findByCommandIds(ids);
        } else if (status != null) members = memberService.findByStatus(status);
        else if (commandId != null) members = memberService.findByCommandId(commandId);
        else if (rank != null) members = memberService.findByRank(rank);
        else members = memberService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(members));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> getById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok(memberService.findById(id)));
    }

    @PostMapping
    @Audited(action = "MEMBER_CREATE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> create(
            @RequestBody MemberRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        // A zone-scoped caller may only register members into their own zone.
        accessGuard.assertCanTargetCommand(authRole, authCommand, request.getCommandId());
        return ResponseEntity.ok(ApiResponse.ok("Member registered", memberService.create(request)));
    }

    @PutMapping("/{id}")
    @Audited(action = "MEMBER_UPDATE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> update(
            @PathVariable Long id,
            @RequestBody MemberRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        // Prevent a zone-scoped caller from moving the member out of their zone via the command field.
        accessGuard.assertCanTargetCommand(authRole, authCommand, request.getCommandId());
        return ResponseEntity.ok(ApiResponse.ok("Member updated", memberService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @Audited(action = "MEMBER_STATUS_CHANGE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> updateStatus(
            @PathVariable Long id,
            @RequestBody MemberStatusUpdateRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok("Status updated", memberService.updateStatus(id, request)));
    }

    @PatchMapping("/{id}/rank")
    @Audited(action = "MEMBER_RANK_CHANGE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> promoteRank(
            @PathVariable Long id,
            @RequestBody MemberRankUpdateRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok("Rank updated", memberService.promoteRank(id, request)));
    }

    @GetMapping("/{id}/status-history")
    public ResponseEntity<ApiResponse<List<MemberStatusHistory>>> getStatusHistory(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok(memberService.getStatusHistory(id)));
    }

    @GetMapping("/{id}/rank-history")
    public ResponseEntity<ApiResponse<List<MemberRankHistory>>> getRankHistory(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok(memberService.getRankHistory(id)));
    }

    @PatchMapping("/{id}/transfer")
    @Audited(action = "MEMBER_TRANSFER", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> transfer(
            @PathVariable Long id,
            @RequestBody MemberTransferRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        // A zone-scoped caller may only transfer members to a command inside their own zone.
        accessGuard.assertCanTargetCommand(authRole, authCommand, request.getToCommandId());
        return ResponseEntity.ok(ApiResponse.ok("Member transferred", memberService.transfer(id, request)));
    }

    @GetMapping("/{id}/transfer-history")
    public ResponseEntity<ApiResponse<List<MemberTransferHistory>>> getTransferHistory(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok(memberService.getTransferHistory(id)));
    }

    @PatchMapping("/{id}/responsibility")
    @Audited(action = "MEMBER_RESPONSIBILITY_CHANGE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> updateResponsibility(
            @PathVariable Long id,
            @RequestBody MemberResponsibilityUpdateRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok("Responsibility updated", memberService.updateResponsibility(id, request)));
    }

    @GetMapping("/{id}/responsibility-history")
    public ResponseEntity<ApiResponse<List<MemberResponsibilityHistory>>> getResponsibilityHistory(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok(memberService.getResponsibilityHistory(id)));
    }

    @PatchMapping("/{id}/member-role")
    @Audited(action = "MEMBER_ROLE_CHANGE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> updateMemberRole(
            @PathVariable Long id,
            @RequestBody MemberRoleUpdateRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok("Role updated", memberService.updateMemberRole(id, request)));
    }

    @GetMapping("/{id}/role-history")
    public ResponseEntity<ApiResponse<List<MemberRoleHistory>>> getRoleHistory(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok(memberService.getRoleHistory(id)));
    }

    @GetMapping("/find-by-email")
    public ResponseEntity<ApiResponse<Member>> findByEmail(
            @RequestParam String email,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        return memberService.findByEmail(email)
                .map(m -> {
                    accessGuard.assertCanAccessMember(authRole, authCommand, m.getId());
                    return ResponseEntity.ok(ApiResponse.ok(m));
                })
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("No member registered with this email.")));
    }

    /** ── Unified Individual Lifetime Record ── */
    @GetMapping("/{id}/lifetime")
    public ResponseEntity<ApiResponse<LifetimeSummaryDTO>> getLifetime(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        return ResponseEntity.ok(ApiResponse.ok(lifetimeService.buildLifetime(id)));
    }

    @DeleteMapping("/{id}")
    @Audited(action = "MEMBER_DELETE", targetTable = "members")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCanAccessMember(authRole, authCommand, id);
        memberService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Member deleted", null));
    }
}
