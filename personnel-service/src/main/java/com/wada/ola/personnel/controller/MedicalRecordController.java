package com.wada.ola.personnel.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.personnel.dto.MedicalRecordRequest;
import com.wada.ola.personnel.entity.MedicalRecord;
import com.wada.ola.personnel.service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/personnel")
public class MedicalRecordController {

    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    @GetMapping("/members/{memberId}/medical-records")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.ok(service.findByMember(memberId)));
    }

    @PostMapping("/members/{memberId}/medical-records")
    @Audited(action = "MEDICAL_RECORD_CREATE", targetTable = "medical_records")
    public ResponseEntity<ApiResponse<MedicalRecord>> create(@PathVariable Long memberId,
                                                              @RequestBody MedicalRecordRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Medical record added", service.create(memberId, request)));
    }

    @DeleteMapping("/medical-records/{id}")
    @Audited(action = "MEDICAL_RECORD_DELETE", targetTable = "medical_records")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Record deleted", null));
    }
}
