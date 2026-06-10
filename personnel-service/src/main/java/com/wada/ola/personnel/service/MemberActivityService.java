package com.wada.ola.personnel.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.personnel.dto.MemberActivityRequest;
import com.wada.ola.personnel.entity.Member;
import com.wada.ola.personnel.entity.MemberActivity;
import com.wada.ola.personnel.repository.MemberActivityRepository;
import com.wada.ola.personnel.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberActivityService {

    private final MemberActivityRepository activityRepository;
    private final MemberRepository memberRepository;

    public MemberActivityService(MemberActivityRepository activityRepository,
                                  MemberRepository memberRepository) {
        this.activityRepository = activityRepository;
        this.memberRepository = memberRepository;
    }

    public List<MemberActivity> getActivities(Long memberId) {
        return activityRepository.findByMemberIdOrderByActivityDateDesc(memberId);
    }

    @Transactional
    public MemberActivity create(Long memberId, MemberActivityRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", memberId));
        MemberActivity activity = new MemberActivity();
        activity.setMember(member);
        activity.setTitle(request.getTitle());
        activity.setDescription(request.getDescription());
        activity.setActivityDate(request.getActivityDate());
        activity.setType(request.getType());
        return activityRepository.save(activity);
    }

    public void delete(Long activityId) {
        if (!activityRepository.existsById(activityId)) {
            throw new ResourceNotFoundException("Activity", activityId);
        }
        activityRepository.deleteById(activityId);
    }
}
