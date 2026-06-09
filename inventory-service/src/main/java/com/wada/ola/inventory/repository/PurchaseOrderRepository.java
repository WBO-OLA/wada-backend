package com.wada.ola.inventory.repository;

import com.wada.ola.inventory.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByStatusOrderByCreatedAtDesc(PurchaseOrder.OrderStatus status);
    List<PurchaseOrder> findAllByOrderByCreatedAtDesc();
}
