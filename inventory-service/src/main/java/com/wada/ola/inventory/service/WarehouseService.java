package com.wada.ola.inventory.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.inventory.dto.WarehouseRequest;
import com.wada.ola.inventory.entity.Warehouse;
import com.wada.ola.inventory.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    public List<Warehouse> findActive() {
        return warehouseRepository.findByActive(true);
    }

    public Warehouse findById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
    }

    public Warehouse create(WarehouseRequest request) {
        Warehouse warehouse = new Warehouse();
        applyRequest(warehouse, request);
        return warehouseRepository.save(warehouse);
    }

    public Warehouse update(Long id, WarehouseRequest request) {
        Warehouse warehouse = findById(id);
        applyRequest(warehouse, request);
        return warehouseRepository.save(warehouse);
    }

    public void deactivate(Long id) {
        Warehouse warehouse = findById(id);
        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
    }

    private void applyRequest(Warehouse warehouse, WarehouseRequest request) {
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setDescription(request.getDescription());
    }
}
