package kg.bdc.warehouse.service;

import kg.bdc.warehouse.dto.*;
import kg.bdc.warehouse.entity.*;
import kg.bdc.warehouse.repository.*;
import kg.bdc.warehouse.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final DriverRepository driverRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryMovementRepository movementRepository;
    private final WebSocketNotificationService wsService;

    @Transactional
    public DispatchDto create(CreateDispatchRequest req) {
        Warehouse warehouse = warehouseRepository.findById(req.getWarehouseId())
                .orElseThrow(() -> new NoSuchElementException("Склад не найден"));
        Driver driver = driverRepository.findById(req.getDriverId())
                .orElseThrow(() -> new NoSuchElementException("Водитель не найден"));

        // Build dispatch
        Dispatch dispatch = Dispatch.builder()
                .warehouse(warehouse)
                .driver(driver)
                .invoiceNumber(generateInvoice())
                .status("CREATED")
                .dispatchDate(LocalDateTime.now())
                .build();

        List<DispatchItem> dispatchItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (DispatchItemRequest itemReq : req.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new NoSuchElementException("Продукт не найден: " + itemReq.getProductId()));

            // Reduce inventory
            Inventory inventory = inventoryRepository.findByWarehouseId(req.getWarehouseId())
                    .stream()
                    .filter(inv -> inv.getProduct().getId().equals(itemReq.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Товар отсутствует на складе: " + product.getName()));

            int boxes = itemReq.getQuantityBoxes() != null ? itemReq.getQuantityBoxes() : 0;
            int pieces = itemReq.getQuantityPieces() != null ? itemReq.getQuantityPieces() : 0;

            if (inventory.getQuantityBoxes() < boxes) {
                throw new IllegalStateException(
                        "Недостаточно коробок для " + product.getName() +
                        ": доступно " + inventory.getQuantityBoxes() + ", запрошено " + boxes);
            }

            inventory.setQuantityBoxes(inventory.getQuantityBoxes() - boxes);
            inventory.setQuantityPieces(inventory.getQuantityPieces() - pieces);
            inventoryRepository.save(inventory);

            // Build item
            BigDecimal lineTotal = product.getDriverPrice()
                    .multiply(BigDecimal.valueOf(boxes));
            total = total.add(lineTotal);

            DispatchItem di = DispatchItem.builder()
                    .dispatch(dispatch)
                    .product(product)
                    .quantityBoxes(boxes)
                    .quantityPieces(pieces)
                    .price(product.getDriverPrice())
                    .build();
            dispatchItems.add(di);

            // Movement record
            InventoryMovement movement = InventoryMovement.builder()
                    .warehouse(warehouse)
                    .product(product)
                    .movementType("DISPATCH")
                    .quantityBoxes(boxes)
                    .quantityPieces(pieces)
                    .comment("Выдача водителю " + driver.getFullName())
                    .build();
            movementRepository.save(movement);

            // Check low stock
            if (inventory.getQuantityBoxes() <= 5) {
                wsService.notifyLowStock(product.getId(), product.getName());
            }
        }

        dispatch.setTotalAmount(total);
        dispatch.setItems(dispatchItems);
        Dispatch saved = dispatchRepository.save(dispatch);

        // Increase driver debt
        driver.setDebt(driver.getDebt().add(total));
        driverRepository.save(driver);

        // Notify WebSocket
        wsService.notifyDispatchCreated(saved.getId());
        wsService.notifyDashboardUpdated();

        return toDto(saved);
    }

    public List<DispatchDto> getDispatches(Long warehouseId, Long driverId) {
        List<Dispatch> list;
        if (warehouseId != null && driverId != null) {
            list = dispatchRepository.findByWarehouseIdAndDriverId(warehouseId, driverId);
        } else if (warehouseId != null) {
            list = dispatchRepository.findByWarehouseId(warehouseId);
        } else if (driverId != null) {
            list = dispatchRepository.findByDriverId(driverId);
        } else {
            list = dispatchRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public DispatchDto getById(Long id) {
        return dispatchRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Выдача не найдена: " + id));
    }

    @Transactional
    public DispatchDto updateStatus(Long id, UpdateDispatchStatusRequest req) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Выдача не найдена: " + id));

        String newStatus = req.getStatus();
        if (!List.of("CREATED", "ISSUED", "CANCELED").contains(newStatus)) {
            throw new IllegalArgumentException("Неверный статус: " + newStatus);
        }

        dispatch.setStatus(newStatus);
        Dispatch saved = dispatchRepository.save(dispatch);
        wsService.notifyDashboardUpdated();
        return toDto(saved);
    }

    private String generateInvoice() {
        return "INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    private DispatchDto toDto(Dispatch d) {
        List<DispatchItemDto> items = new ArrayList<>();
        if (d.getItems() != null) {
            items = d.getItems().stream()
                    .map(i -> DispatchItemDto.builder()
                            .id(i.getId())
                            .productId(i.getProduct().getId())
                            .productName(i.getProduct().getName())
                            .quantityBoxes(i.getQuantityBoxes())
                            .quantityPieces(i.getQuantityPieces())
                            .price(i.getPrice())
                            .build())
                    .toList();
        }

        return DispatchDto.builder()
                .id(d.getId())
                .warehouseId(d.getWarehouse() != null ? d.getWarehouse().getId() : null)
                .driverId(d.getDriver() != null ? d.getDriver().getId() : null)
                .driverName(d.getDriver() != null ? d.getDriver().getFullName() : null)
                .invoiceNumber(d.getInvoiceNumber())
                .totalAmount(d.getTotalAmount())
                .dispatchDate(d.getDispatchDate())
                .status(d.getStatus())
                .items(items)
                .build();
    }
}
