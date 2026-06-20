package kg.bdc.warehouse.service;

import kg.bdc.warehouse.dto.*;
import kg.bdc.warehouse.entity.Inventory;
import kg.bdc.warehouse.entity.InventoryMovement;
import kg.bdc.warehouse.repository.InventoryMovementRepository;
import kg.bdc.warehouse.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;

    public List<InventoryDto> getInventory(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<InventoryDto> getLowStock(Long warehouseId) {
        return inventoryRepository.findLowStock(warehouseId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<MovementDto> getMovements(Long warehouseId) {
        return movementRepository.findByWarehouseIdOrderByCreatedAtDesc(warehouseId)
                .stream()
                .map(this::toMovementDto)
                .toList();
    }

    private InventoryDto toDto(Inventory i) {
        return InventoryDto.builder()
                .id(i.getId())
                .productId(i.getProduct().getId())
                .productName(i.getProduct().getName())
                .barcode(i.getProduct().getBarcode())
                .piecesPerBox(i.getProduct().getPiecesPerBox())
                .quantityBoxes(i.getQuantityBoxes())
                .quantityPieces(i.getQuantityPieces())
                .driverPrice(i.getProduct().getDriverPrice())
                .updatedAt(i.getUpdatedAt())
                .build();
    }

    private MovementDto toMovementDto(InventoryMovement m) {
        return MovementDto.builder()
                .id(m.getId())
                .movementType(m.getMovementType())
                .productName(m.getProduct().getName())
                .quantityBoxes(m.getQuantityBoxes())
                .quantityPieces(m.getQuantityPieces())
                .comment(m.getComment())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
