package com.wada.ola.personnel.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.personnel.entity.Member;
import com.wada.ola.personnel.entity.MemberDocument;
import com.wada.ola.personnel.repository.MemberDocumentRepository;
import com.wada.ola.personnel.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Value("${app.upload.dir:./uploads/personnel}")
    private String uploadDir;

    private final MemberRepository memberRepository;
    private final MemberDocumentRepository documentRepository;

    public DocumentService(MemberRepository memberRepository,
                            MemberDocumentRepository documentRepository) {
        this.memberRepository = memberRepository;
        this.documentRepository = documentRepository;
    }

    public List<MemberDocument> listByMember(Long memberId) {
        return documentRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public MemberDocument findById(Long docId) {
        return documentRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", docId));
    }

    public MemberDocument upload(Long memberId, MultipartFile file,
                                  String description, String uploadedBy) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", memberId));

        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + (ext != null ? "." + ext : "");
        Files.copy(file.getInputStream(), uploadPath.resolve(storedName),
                StandardCopyOption.REPLACE_EXISTING);

        MemberDocument doc = new MemberDocument();
        doc.setMember(member);
        doc.setFileName(file.getOriginalFilename());
        doc.setStoredFileName(storedName);
        doc.setFileType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setDescription(description);
        doc.setUploadedBy(uploadedBy);
        return documentRepository.save(doc);
    }

    public Resource download(Long docId) {
        MemberDocument doc = findById(docId);
        Path filePath = Paths.get(uploadDir).resolve(doc.getStoredFileName());
        Resource resource = new FileSystemResource(filePath);
        if (!resource.exists()) {
            throw new ResourceNotFoundException("File not found on disk for document id: " + docId);
        }
        return resource;
    }

    public void delete(Long docId) throws IOException {
        MemberDocument doc = findById(docId);
        Path filePath = Paths.get(uploadDir).resolve(doc.getStoredFileName());
        Files.deleteIfExists(filePath);
        documentRepository.deleteById(docId);
    }
}
