package com.wada.ola.personnel.service;

import com.wada.ola.personnel.dto.PersonnelSummaryDTO;
import com.wada.ola.personnel.entity.Member;
import com.wada.ola.personnel.entity.Member.MemberStatus;
import com.wada.ola.personnel.repository.MemberRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonnelReportService {

    private final MemberRepository memberRepository;

    public PersonnelReportService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public PersonnelSummaryDTO getSummary() {
        return getSummary(null);
    }

    public PersonnelSummaryDTO getSummary(Long commandId) {
        List<Member> all = commandId != null
                ? memberRepository.findByCommandId(commandId)
                : memberRepository.findAll();
        PersonnelSummaryDTO dto = new PersonnelSummaryDTO();
        dto.setTotalMembers(all.size());
        dto.setActive(all.stream().filter(m -> m.getStatus() == MemberStatus.ACTIVE).count());
        dto.setInjured(all.stream().filter(m -> m.getStatus() == MemberStatus.INJURED).count());
        dto.setRetired(all.stream().filter(m -> m.getStatus() == MemberStatus.RETIRED).count());
        dto.setPassedAway(all.stream().filter(m -> m.getStatus() == MemberStatus.PASSED_AWAY).count());
        dto.setByRank(all.stream().collect(
                Collectors.groupingBy(m -> m.getRank().name(), Collectors.counting())));
        dto.setByCommand(all.stream().collect(
                Collectors.groupingBy(m -> m.getCommand() != null ? m.getCommand().getName() : "Unassigned", Collectors.counting())));
        return dto;
    }
}
