package kg.bdc.warehouse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.bdc.warehouse.dto.*;
import kg.bdc.warehouse.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Остатки и движение товаров на складе")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(
        summary = "Остатки на складе",
        description = "Возвращает список всех товаров с остатками по указанному складу. " +
                      "WebSocket `/topic/inventory/low-stock` уведомляет о товарах с низким остатком.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Список остатков",
                content = @Content(schema = @Schema(implementation = InventoryDto.class)))
        }
    )
    public ResponseEntity<List<InventoryDto>> getInventory(
            @Parameter(description = "ID склада", example = "1", required = true)
            @RequestParam("warehouse_id") Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getInventory(warehouseId));
    }

    @GetMapping("/low-stock")
    @Operation(
        summary = "Товары с низким остатком",
        description = "Возвращает товары, у которых остаток ≤ 5 коробок. " +
                      "При каждой выдаче, если остаток падает ниже порога, отправляется WS событие в `/topic/inventory/low-stock`",
        responses = {
            @ApiResponse(responseCode = "200", description = "Список товаров с низким остатком",
                content = @Content(schema = @Schema(implementation = InventoryDto.class)))
        }
    )
    public ResponseEntity<List<InventoryDto>> getLowStock(
            @Parameter(description = "ID склада", example = "1", required = true)
            @RequestParam("warehouse_id") Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getLowStock(warehouseId));
    }

    @GetMapping("/movements")
    @Operation(
        summary = "Движение товаров",
        description = "История всех движений товара (поставки, выдачи, возвраты) по складу. Отсортировано по убыванию даты.",
        responses = {
            @ApiResponse(responseCode = "200", description = "История движений",
                content = @Content(schema = @Schema(implementation = MovementDto.class)))
        }
    )
    public ResponseEntity<List<MovementDto>> getMovements(
            @Parameter(description = "ID склада", example = "1", required = true)
            @RequestParam("warehouse_id") Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getMovements(warehouseId));
    }
}
