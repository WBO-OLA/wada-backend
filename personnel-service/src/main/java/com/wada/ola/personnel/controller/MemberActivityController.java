package com.wada.ola.personnel.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.personnel.dto.MemberActivityRequest;
import com.wada.ola.personnel.entity.MemberActivity;
import com.wada.ola.personnel.service.MemberActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personnel")
public class MemberActivityController {

    private final MemberActivityService activityService;

    public MemberActivityController(MemberActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/members/{memberId}/activities")
    public ResponseEntity<ApiResponse<List<MemberActivity>>> getActivities(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.ok(activityService.getActivities(memberId)));
    }

    @PostMapping("/members/{memberId}/activities")
    public ResponseEntity<ApiResponse<MemberActivity>> create(@PathVariable Long memberId,
                                                               @RequestBody MemberActivityRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Activity added", activityService.create(memberId, request)));
    }

    @DeleteMapping("/activities/{activityId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long activityId) {
        activityService.delete(activityId);
        return ResponseEntity.ok(ApiResponse.ok("Activity deleted", null));
    }
}
