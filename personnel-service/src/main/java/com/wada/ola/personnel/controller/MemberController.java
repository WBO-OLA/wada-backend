package com.wada.ola.personnel.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.common.dto.ApiResponse;
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
import com.wada.ola.personnel.service.CommandService;
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
    private final CommandService commandService;

    public MemberController(MemberService memberService, CommandService commandService) {
        this.memberService = memberService;
        this.commandService = commandService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Member>>> getAll(
            @RequestParam(required = false) Member.MemberStatus status,
            @RequestParam(required = false) Long commandId,
            @RequestParam(required = false) Member.MilitaryRank rank,
            @RequestParam(required = false) String commandIds,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {

        boolean isGlobal = authRole == null || authRole.equals("CHIEF") || authRole.equals("ADMIN");

        if (!isGlobal && authCommand != null && !authCommand.isBlank()) {
            long scopedRoot = Long.parseLong(authCommand);
            List<Long> scopedIds = commandService.getAllDescendantIds(scopedRoot);
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
    public ResponseEntity<ApiResponse<Member>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.findById(id)));
    }

    @PostMapping
    @Audited(action = "MEMBER_CREATE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> create(@RequestBody MemberRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Member registered", memberService.create(request)));
    }

    @PutMapping("/{id}")
    @Audited(action = "MEMBER_UPDATE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> update(@PathVariable Long id,
                                                       @RequestBody MemberRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Member updated", memberService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @Audited(action = "MEMBER_STATUS_CHANGE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> updateStatus(@PathVariable Long id,
                                                             @RequestBody MemberStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", memberService.updateStatus(id, request)));
    }

    @PatchMapping("/{id}/rank")
    @Audited(action = "MEMBER_RANK_CHANGE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> promoteRank(@PathVariable Long id,
                                                            @RequestBody MemberRankUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Rank updated", memberService.promoteRank(id, request)));
    }

    @GetMapping("/{id}/status-history")
    public ResponseEntity<ApiResponse<List<MemberStatusHistory>>> getStatusHistory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getStatusHistory(id)));
    }

    @GetMapping("/{id}/rank-history")
    public ResponseEntity<ApiResponse<List<MemberRankHistory>>> getRankHistory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getRankHistory(id)));
    }

    @PatchMapping("/{id}/transfer")
    @Audited(action = "MEMBER_TRANSFER", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> transfer(@PathVariable Long id,
                                                         @RequestBody MemberTransferRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Member transferred", memberService.transfer(id, request)));
    }

    @GetMapping("/{id}/transfer-history")
    public ResponseEntity<ApiResponse<List<MemberTransferHistory>>> getTransferHistory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getTransferHistory(id)));
    }

    @PatchMapping("/{id}/responsibility")
    @Audited(action = "MEMBER_RESPONSIBILITY_CHANGE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> updateResponsibility(@PathVariable Long id,
                                                                     @RequestBody MemberResponsibilityUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Responsibility updated", memberService.updateResponsibility(id, request)));
    }

    @GetMapping("/{id}/responsibility-history")
    public ResponseEntity<ApiResponse<List<MemberResponsibilityHistory>>> getResponsibilityHistory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getResponsibilityHistory(id)));
    }

    @PatchMapping("/{id}/member-role")
    @Audited(action = "MEMBER_ROLE_CHANGE", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> updateMemberRole(@PathVariable Long id,
                                                                 @RequestBody MemberRoleUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Role updated", memberService.updateMemberRole(id, request)));
    }

    @GetMapping("/{id}/role-history")
    public ResponseEntity<ApiResponse<List<MemberRoleHistory>>> getRoleHistory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getRoleHistory(id)));
    }

    @GetMapping("/find-by-email")
    public ResponseEntity<ApiResponse<Member>> findByEmail(@RequestParam String email) {
        return memberService.findByEmail(email)
                .map(m -> ResponseEntity.ok(ApiResponse.ok(m)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("No member registered with this email.")));
    }

    @DeleteMapping("/{id}")
    @Audited(action = "MEMBER_DELETE", targetTable = "members")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Member deleted", null));
    }
}
