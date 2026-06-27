package com.wada.ola.personnel.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.personnel.dto.CommandRequest;
import com.wada.ola.personnel.entity.Command;
import com.wada.ola.personnel.service.CommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/personnel/commands")
public class CommandController {

    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Command>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(commandService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Command>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(commandService.findById(id)));
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<Command>>> getChildren(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(commandService.findChildren(id)));
    }

    @GetMapping("/{id}/descendants")
    public ResponseEntity<ApiResponse<List<Long>>> getDescendantIds(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(commandService.getAllDescendantIds(id)));
    }

    @PostMapping
    @Audited(action = "COMMAND_CREATE", targetTable = "commands")
    public ResponseEntity<ApiResponse<Command>> create(@RequestBody CommandRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Command created", commandService.create(request)));
    }

    @PutMapping("/{id}")
    @Audited(action = "COMMAND_UPDATE", targetTable = "commands")
    public ResponseEntity<ApiResponse<Command>> update(@PathVariable Long id,
                                                        @RequestBody CommandRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Command updated", commandService.update(id, request)));
    }

    @PatchMapping("/{id}/commander")
    @Audited(action = "COMMAND_ASSIGN_COMMANDER", targetTable = "commands")
    public ResponseEntity<ApiResponse<Command>> assignCommander(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Long memberId = body.get("memberId") != null
                ? Long.valueOf(body.get("memberId").toString()) : null;
        return ResponseEntity.ok(ApiResponse.ok("Commander assigned", commandService.assignCommander(id, memberId)));
    }

    @DeleteMapping("/{id}")
    @Audited(action = "COMMAND_DELETE", targetTable = "commands")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        commandService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Command deleted", null));
    }
}
