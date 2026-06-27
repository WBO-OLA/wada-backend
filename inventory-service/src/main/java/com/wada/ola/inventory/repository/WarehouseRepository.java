package com.wada.ola.inventory.repository;

import com.wada.ola.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    List<Warehouse> findByActive(boolean active);
    List<Warehouse> findByCommandId(Long commandId);
    List<Warehouse> findByActiveAndCommandId(boolean active, Long commandId);
    Optional<Warehouse> findByName(String name);
}
