package com.wada.ola.inventory.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.inventory.dto.AssetAssignmentRequest;
import com.wada.ola.inventory.entity.AssetAssignment;
import com.wada.ola.inventory.entity.AssetAssignment.AssignmentStatus;
import com.wada.ola.inventory.repository.AssetAssignmentRepository;
import com.wada.ola.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssetAssignmentService {

    private final AssetAssignmentRepository repository;
    private final ItemRepository itemRepository;

    public AssetAssignmentService(AssetAssignmentRepository repository, ItemRepository itemRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
    }

    public List<AssetAssignment> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public List<AssetAssignment> findByItem(Long itemId) {
        return repository.findByItemIdOrderByCreatedAtDesc(itemId);
    }

    public List<AssetAssignment> findActiveByMember(Long memberId) {
        return repository.findByMemberIdAndStatusOrderByCreatedAtDesc(memberId, AssignmentStatus.ASSIGNED);
    }

    @Transactional
    public AssetAssignment assign(AssetAssignmentRequest request) {
        var item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item", request.getItemId()));
        AssetAssignment assignment = new AssetAssignment();
        assignment.setItem(item);
        assignment.setMemberId(request.getMemberId());
        assignment.setMemberName(request.getMemberName());
        assignment.setAssignedBy(request.getAssignedBy());
        assignment.setNotes(request.getNotes());
        return repository.save(assignment);
    }

    @Transactional
    public AssetAssignment returnItem(Long assignmentId) {
        AssetAssignment assignment = repository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("AssetAssignment", assignmentId));
        if (assignment.getStatus() == AssignmentStatus.RETURNED) {
            throw new IllegalStateException("Asset already returned.");
        }
        assignment.setStatus(AssignmentStatus.RETURNED);
        assignment.setReturnedAt(LocalDateTime.now());
        return repository.save(assignment);
    }
}
