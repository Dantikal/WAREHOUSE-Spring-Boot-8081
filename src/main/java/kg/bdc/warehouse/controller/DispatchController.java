package kg.bdc.warehouse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.bdc.warehouse.dto.*;
import kg.bdc.warehouse.service.DispatchService;
import kg.bdc.warehouse.service.WarehouseContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/dispatch")
@RequiredArgsConstructor
@Tag(name = "Dispatch", description = "Выдача товаров водителям")
public class DispatchController {

    private final DispatchService dispatchService;
    private final WarehouseContextService warehouseContextService;

    @PostMapping
    @Operation(
        summary = "Создать выдачу",
        description = """
                Создаёт накладную на выдачу товара водителю. 
                
                **Бизнес-логика:**
                - Уменьшает остаток товара на складе
                - Увеличивает долг водителя на сумму выдачи
                - Создаёт накладную с авто-номером
                - Записывает движение товара для каждой позиции
                - Отправляет WS событие → `/topic/dispatch/created`
                - При низком остатке отправляет → `/topic/inventory/low-stock`
                - Обновляет дашборд → `/topic/dashboard/update`
                """,
        responses = {
            @ApiResponse(responseCode = "201", description = "Выдача создана",
                content = @Content(schema = @Schema(implementation = DispatchDto.class))),
            @ApiResponse(responseCode = "400", description = "Недостаточно товара на складе или неверные данные"),
            @ApiResponse(responseCode = "404", description = "Склад / водитель / товар не найден")
        }
    )
    public ResponseEntity<DispatchDto> create(
            HttpServletRequest servletRequest,
            @Valid @RequestBody CreateDispatchRequest request) {
        Long warehouseId = warehouseContextService.requireWarehouseId(servletRequest);
        request.setWarehouseId(warehouseId);
        DispatchDto result = dispatchService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    @Operation(
        summary = "Список выдач",
        description = "Возвращает выдачи по складу и/или водителю. Оба параметра опциональны — если не указан ни один, возвращаются все выдачи.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Список выдач",
                content = @Content(schema = @Schema(implementation = DispatchDto.class)))
        }
    )
    public ResponseEntity<List<DispatchDto>> getDispatches(
            HttpServletRequest request,
            @Parameter(description = "ID водителя", example = "5") @RequestParam(value = "driver_id", required = false) Long driverId) {
        Long warehouseId = warehouseContextService.requireWarehouseId(request);
        return ResponseEntity.ok(dispatchService.getDispatches(warehouseId, driverId));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Получить выдачу по ID",
        description = "Возвращает полную информацию о выдаче, включая все позиции товаров",
        responses = {
            @ApiResponse(responseCode = "200", description = "Выдача найдена",
                content = @Content(schema = @Schema(implementation = DispatchDto.class))),
            @ApiResponse(responseCode = "404", description = "Выдача не найдена")
        }
    )
    public ResponseEntity<DispatchDto> getById(
            HttpServletRequest request,
            @Parameter(description = "ID выдачи", example = "1") @PathVariable Long id) {
        Long warehouseId = warehouseContextService.requireWarehouseId(request);
        return ResponseEntity.ok(dispatchService.getById(id, warehouseId));
    }

    @PutMapping("/{id}/status")
    @Operation(
        summary = "Изменить статус выдачи",
        description = """
                Изменяет статус выдачи. Допустимые переходы:
                - `CREATED` → `ISSUED` — товар физически передан водителю
                - `CREATED` → `CANCELED` — отмена выдачи
                
                После изменения обновляет дашборд через WebSocket.
                """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Статус обновлён",
                content = @Content(schema = @Schema(implementation = DispatchDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный статус"),
            @ApiResponse(responseCode = "404", description = "Выдача не найдена")
        }
    )
    public ResponseEntity<DispatchDto> updateStatus(
            HttpServletRequest servletRequest,
            @Parameter(description = "ID выдачи", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateDispatchStatusRequest request) {
        Long warehouseId = warehouseContextService.requireWarehouseId(servletRequest);
        return ResponseEntity.ok(dispatchService.updateStatus(id, request, warehouseId));
    }

    @PostMapping("/{id}/confirm")
    @Operation(
        summary = "Подтвердить выдачу",
        description = "Подтверждает выдачу со стороны drivers-service. Идемпотентно переводит выдачу в статус `ISSUED`.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Выдача подтверждена",
                content = @Content(schema = @Schema(implementation = DispatchDto.class))),
            @ApiResponse(responseCode = "404", description = "Выдача не найдена")
        }
    )
    public ResponseEntity<DispatchDto> confirm(
            HttpServletRequest request,
            @Parameter(description = "ID выдачи", example = "1") @PathVariable Long id) {
        Long warehouseId = warehouseContextService.requireWarehouseId(request);
        return ResponseEntity.ok(dispatchService.confirm(id, warehouseId));
    }
}
