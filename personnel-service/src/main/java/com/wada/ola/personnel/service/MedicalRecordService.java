package com.wada.ola.personnel.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.personnel.dto.MedicalRecordRequest;
import com.wada.ola.personnel.entity.MedicalRecord;
import com.wada.ola.personnel.repository.MedicalRecordRepository;
import com.wada.ola.personnel.repository.MemberRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository repository;
    private final MemberRepository memberRepository;

    public MedicalRecordService(MedicalRecordRepository repository, MemberRepository memberRepository) {
        this.repository = repository;
        this.memberRepository = memberRepository;
    }

    public List<MedicalRecord> findByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member", memberId);
        }
        return repository.findByMemberIdOrderByRecordDateDesc(memberId);
    }

    public MedicalRecord create(Long memberId, MedicalRecordRequest request) {
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", memberId));
        MedicalRecord record = new MedicalRecord();
        record.setMember(member);
        record.setRecordDate(request.getRecordDate());
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setPhysician(request.getPhysician());
        record.setConfidential(request.isConfidential());
        record.setNotes(request.getNotes());
        return repository.save(record);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("MedicalRecord", id);
        }
        repository.deleteById(id);
    }
}
