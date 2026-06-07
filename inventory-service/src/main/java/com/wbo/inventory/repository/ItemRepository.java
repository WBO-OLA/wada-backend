package com.wbo.inventory.repository;

import com.wbo.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findBySku(String sku);
    List<Item> findByCategory(String category);
    List<Item> findByQuantityLessThan(Integer threshold);
}
