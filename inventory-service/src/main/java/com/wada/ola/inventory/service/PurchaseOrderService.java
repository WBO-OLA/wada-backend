package com.wada.ola.inventory.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.inventory.dto.PurchaseOrderRequest;
import com.wada.ola.inventory.entity.PurchaseOrder;
import com.wada.ola.inventory.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository repository;

    public PurchaseOrderService(PurchaseOrderRepository repository) {
        this.repository = repository;
    }

    public List<PurchaseOrder> findAll(PurchaseOrder.OrderStatus status, Long commandId) {
        if (commandId != null && status != null)
            return repository.findByCommandIdAndStatusOrderByCreatedAtDesc(commandId, status);
        if (commandId != null)
            return repository.findByCommandIdOrderByCreatedAtDesc(commandId);
        if (status != null)
            return repository.findByStatusOrderByCreatedAtDesc(status);
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public PurchaseOrder findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", id));
    }

    public PurchaseOrder create(PurchaseOrderRequest req) {
        PurchaseOrder po = new PurchaseOrder();
        po.setOrderNumber("PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        po.setSupplier(req.getSupplier());
        po.setItemName(req.getItemName());
        po.setItemSku(req.getItemSku());
        po.setQuantity(req.getQuantity());
        po.setUnitPrice(req.getUnitPrice());
        po.setTotalAmount(req.getUnitPrice().multiply(BigDecimal.valueOf(req.getQuantity())));
        po.setOrderedBy(req.getOrderedBy());
        po.setExpectedDeliveryDate(req.getExpectedDeliveryDate());
        po.setNotes(req.getNotes());
        po.setCommandId(req.getCommandId());
        return repository.save(po);
    }

    public PurchaseOrder updateStatus(Long id, PurchaseOrder.OrderStatus status) {
        PurchaseOrder po = findById(id);
        po.setStatus(status);
        return repository.save(po);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
