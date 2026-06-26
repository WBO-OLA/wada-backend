package com.wada.ola.inventory.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.inventory.dto.ItemRequest;
import com.wada.ola.inventory.entity.Item;
import com.wada.ola.inventory.entity.Warehouse;
import com.wada.ola.inventory.repository.ItemRepository;
import com.wada.ola.inventory.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;

    public ItemService(ItemRepository itemRepository, WarehouseRepository warehouseRepository) {
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }

    public List<Item> findByCategory(String category) {
        return itemRepository.findByCategory(category);
    }

    public List<Item> findByWarehouse(Long warehouseId) {
        return itemRepository.findByWarehouseId(warehouseId);
    }

    public List<Item> findLowStock(int threshold) {
        return itemRepository.findByQuantityLessThan(threshold);
    }

    public List<Item> findByCommandIds(List<Long> commandIds) {
        if (commandIds == null || commandIds.isEmpty()) return findAll();
        return itemRepository.findByCommandIdIn(commandIds);
    }

    public Item create(ItemRequest request) {
        Item item = new Item();
        applyRequest(item, request);
        return itemRepository.save(item);
    }

    public Item update(Long id, ItemRequest request) {
        Item item = findById(id);
        applyRequest(item, request);
        return itemRepository.save(item);
    }

    public void delete(Long id) {
        itemRepository.delete(findById(id));
    }

    Item save(Item item) {
        return itemRepository.save(item);
    }

    private void applyRequest(Item item, ItemRequest request) {
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setSku(request.getSku());
        item.setQuantity(request.getQuantity() != null ? request.getQuantity() : 0);
        item.setUnitPrice(request.getUnitPrice());
        item.setCategory(request.getCategory());
        if (request.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse", request.getWarehouseId()));
            item.setWarehouse(warehouse);
        } else {
            item.setWarehouse(null);
        }
        item.setCommandId(request.getCommandId());
    }
}
