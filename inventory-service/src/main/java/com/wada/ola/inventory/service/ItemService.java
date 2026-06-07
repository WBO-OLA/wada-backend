package com.wada.ola.inventory.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.inventory.entity.Item;
import com.wada.ola.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }

    public List<Item> findLowStock(int threshold) {
        return itemRepository.findByQuantityLessThan(threshold);
    }

    public Item create(Item item) {
        return itemRepository.save(item);
    }

    public Item update(Long id, Item updated) {
        Item existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setSku(updated.getSku());
        existing.setQuantity(updated.getQuantity());
        existing.setUnitPrice(updated.getUnitPrice());
        existing.setCategory(updated.getCategory());
        return itemRepository.save(existing);
    }

    public void delete(Long id) {
        itemRepository.delete(findById(id));
    }
}

