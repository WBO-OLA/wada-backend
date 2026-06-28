package com.wada.ola.personnel.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.personnel.entity.Member;
import com.wada.ola.personnel.entity.MemberDocument;
import com.wada.ola.personnel.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/api/personnel/members/{memberId}/documents")
    public ResponseEntity<ApiResponse<List<MemberDocument>>> list(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.ok(documentService.listByMember(memberId)));
    }

    @PostMapping("/api/personnel/members/{memberId}/documents")
    @Audited(action = "DOCUMENT_UPLOAD", targetTable = "member_documents")
    public ResponseEntity<ApiResponse<MemberDocument>> upload(
            @PathVariable Long memberId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "uploadedBy", required = false) String uploadedBy) throws IOException {
        MemberDocument doc = documentService.upload(memberId, file, description, uploadedBy);
        return ResponseEntity.ok(ApiResponse.ok("Document uploaded", doc));
    }

    @GetMapping("/api/personnel/documents/{docId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long docId) {
        MemberDocument doc = documentService.findById(docId);
        Resource resource = documentService.download(docId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        doc.getFileType() != null ? doc.getFileType() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/api/personnel/documents/{docId}")
    @Audited(action = "DOCUMENT_DELETE", targetTable = "member_documents")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long docId) throws IOException {
        documentService.delete(docId);
        return ResponseEntity.ok(ApiResponse.ok("Document deleted", null));
    }

    @PostMapping("/api/personnel/members/{memberId}/photo")
    @Audited(action = "MEMBER_PHOTO_UPLOAD", targetTable = "members")
    public ResponseEntity<ApiResponse<Member>> uploadPhoto(
            @PathVariable Long memberId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole) throws IOException {
        if (authRole == null || (!authRole.equals("ADMIN") && !authRole.equals("CHIEF"))) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("Only Admin or Chief can upload profile photos."));
        }
        return ResponseEntity.ok(ApiResponse.ok("Photo updated", documentService.uploadPhoto(memberId, file)));
    }

    @GetMapping("/api/personnel/members/{memberId}/photo")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long memberId) {
        Resource resource = documentService.getPhoto(memberId);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }
}
