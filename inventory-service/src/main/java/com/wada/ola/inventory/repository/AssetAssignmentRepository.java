package com.wada.ola.inventory.repository;

import com.wada.ola.inventory.entity.AssetAssignment;
import com.wada.ola.inventory.entity.AssetAssignment.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssetAssignmentRepository extends JpaRepository<AssetAssignment, Long> {
    List<AssetAssignment> findByItemIdOrderByCreatedAtDesc(Long itemId);
    List<AssetAssignment> findByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, AssignmentStatus status);
    List<AssetAssignment> findAllByOrderByCreatedAtDesc();
}
