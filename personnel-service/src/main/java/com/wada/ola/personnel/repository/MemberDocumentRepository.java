package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.MemberDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberDocumentRepository extends JpaRepository<MemberDocument, Long> {
    List<MemberDocument> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
