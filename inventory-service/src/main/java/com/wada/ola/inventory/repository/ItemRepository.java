package com.wada.ola.inventory.repository;

import com.wada.ola.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findBySku(String sku);
    List<Item> findByCategory(String category);
    List<Item> findByQuantityLessThan(Integer threshold);
    List<Item> findByWarehouseId(Long warehouseId);
}
