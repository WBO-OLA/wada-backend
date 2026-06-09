package com.wada.ola.inventory.service;

import com.wada.ola.inventory.dto.InventorySummaryDTO;
import com.wada.ola.inventory.entity.Item;
import com.wada.ola.inventory.entity.Warehouse;
import com.wada.ola.inventory.repository.ItemRepository;
import com.wada.ola.inventory.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryReportService {

    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;

    public InventoryReportService(ItemRepository itemRepository, WarehouseRepository warehouseRepository) {
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public InventorySummaryDTO getSummary() {
        List<Item> items = itemRepository.findAll();
        List<Warehouse> warehouses = warehouseRepository.findAll();

        InventorySummaryDTO dto = new InventorySummaryDTO();
        dto.setTotalItems(items.size());
        dto.setTotalWarehouses(warehouses.size());
        dto.setActiveWarehouses(warehouses.stream().filter(Warehouse::isActive).count());
        dto.setLowStockCount(items.stream().filter(i -> i.getQuantity() > 0 && i.getQuantity() < 10).count());
        dto.setOutOfStockCount(items.stream().filter(i -> i.getQuantity() == 0).count());
        dto.setTotalStockValue(items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setByCategory(items.stream().collect(
                Collectors.groupingBy(i -> i.getCategory() != null ? i.getCategory() : "Uncategorized",
                        Collectors.counting())));
        return dto;
    }
}
