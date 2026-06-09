package com.wada.ola.personnel.repository;

import com.wada.ola.personnel.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByMemberIdOrderByRecordDateDesc(Long memberId);
}
