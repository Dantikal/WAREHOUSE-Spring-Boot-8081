package kg.bdc.warehouse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.bdc.warehouse.dto.*;
import kg.bdc.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
@Tag(name = "Warehouses", description = "Управление складами и статистика")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/warehouses")
    @Operation(
        summary = "Получить список складов",
        description = "Возвращает список всех складов системы",
        responses = {
            @ApiResponse(responseCode = "200", description = "Список складов",
                content = @Content(schema = @Schema(implementation = WarehouseDto.class)))
        }
    )
    public ResponseEntity<List<WarehouseDto>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAll());
    }

    @GetMapping("/warehouses/{id}")
    @Operation(
        summary = "Получить склад по ID",
        description = "Возвращает подробную информацию об одном складе",
        responses = {
            @ApiResponse(responseCode = "200", description = "Склад найден",
                content = @Content(schema = @Schema(implementation = WarehouseDto.class))),
            @ApiResponse(responseCode = "404", description = "Склад не найден")
        }
    )
    public ResponseEntity<WarehouseDto> getWarehouseById(
            @Parameter(description = "ID склада", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getById(id));
    }

    @GetMapping("/warehouses/{id}/stats")
    @Operation(
        summary = "Статистика склада",
        description = "Возвращает агрегированную статистику: количество товаров, баланс кассы, долг заводу и т.д.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Статистика склада",
                content = @Content(schema = @Schema(implementation = WarehouseStatsDto.class)))
        }
    )
    public ResponseEntity<WarehouseStatsDto> getWarehouseStats(
            @Parameter(description = "ID склада", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getStats(id));
    }

    @GetMapping("/stats/country")
    @Operation(
        summary = "Статистика по стране",
        description = "Агрегированная статистика по всем складам страны",
        responses = {
            @ApiResponse(responseCode = "200", description = "Статистика по стране",
                content = @Content(schema = @Schema(implementation = CountryStatsDto.class)))
        }
    )
    public ResponseEntity<CountryStatsDto> getCountryStats() {
        return ResponseEntity.ok(warehouseService.getCountryStats());
    }

    @GetMapping("/dashboard")
    @Operation(
        summary = "Dashboard — сводка",
        description = "Главный экран: баланс кассы, долги, низкий остаток, новые заявки. " +
                      "Также обновляется в реальном времени через WebSocket `/topic/dashboard/update`",
        responses = {
            @ApiResponse(responseCode = "200", description = "Данные дашборда",
                content = @Content(schema = @Schema(implementation = DashboardDto.class)))
        }
    )
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(warehouseService.getDashboard());
    }
}
