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
import kg.bdc.warehouse.service.CashboxService;
import kg.bdc.warehouse.service.WarehouseContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/cashbox")
@RequiredArgsConstructor
@Tag(name = "Cashbox", description = "Касса и финансовые операции склада")
public class CashboxController {

    private final CashboxService cashboxService;
    private final WarehouseContextService warehouseContextService;

    @GetMapping("/balance")
    @Operation(
        summary = "Баланс кассы",
        description = "Возвращает текущий баланс кассы указанного склада",
        responses = {
            @ApiResponse(responseCode = "200", description = "Баланс кассы",
                content = @Content(schema = @Schema(implementation = CashboxBalanceDto.class))),
            @ApiResponse(responseCode = "404", description = "Касса склада не найдена")
        }
    )
    public ResponseEntity<CashboxBalanceDto> getBalance(HttpServletRequest request) {
        Long warehouseId = warehouseContextService.requireWarehouseId(request);
        return ResponseEntity.ok(cashboxService.getBalance(warehouseId));
    }

    @GetMapping("/transactions")
    @Operation(
        summary = "Операции кассы",
        description = "Возвращает историю финансовых операций кассы. Типы: DRIVER_PAYMENT, FACTORY_PAYMENT, OTHER",
        responses = {
            @ApiResponse(responseCode = "200", description = "История операций",
                content = @Content(schema = @Schema(implementation = CashboxTransactionDto.class)))
        }
    )
    public ResponseEntity<List<CashboxTransactionDto>> getTransactions(HttpServletRequest request) {
        Long warehouseId = warehouseContextService.requireWarehouseId(request);
        return ResponseEntity.ok(cashboxService.getTransactions(warehouseId));
    }

    @PostMapping("/driver-payment")
    @Operation(
        summary = "Платеж водителя",
        description = "Принимает платеж водителя в кассу текущего склада и уменьшает долг водителя.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Платеж принят",
                content = @Content(schema = @Schema(implementation = CashboxTransactionDto.class))),
            @ApiResponse(responseCode = "404", description = "Склад / водитель / касса не найдены")
        }
    )
    public ResponseEntity<CashboxTransactionDto> createDriverPayment(
            HttpServletRequest servletRequest,
            @Valid @RequestBody DriverPaymentRequest request) {
        Long warehouseId = warehouseContextService.requireWarehouseId(servletRequest);
        return ResponseEntity.ok(cashboxService.createDriverPayment(warehouseId, request));
    }

    @PostMapping("/factory-payment")
    @Operation(
        summary = "Платеж заводу",
        description = "Проводит оплату заводу из кассы текущего склада и уменьшает долг склада перед заводом.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Платеж проведен",
                content = @Content(schema = @Schema(implementation = CashboxTransactionDto.class))),
            @ApiResponse(responseCode = "409", description = "Недостаточно средств в кассе")
        }
    )
    public ResponseEntity<CashboxTransactionDto> createFactoryPayment(
            HttpServletRequest servletRequest,
            @Valid @RequestBody FactoryPaymentRequest request) {
        Long warehouseId = warehouseContextService.requireWarehouseId(servletRequest);
        return ResponseEntity.ok(cashboxService.createFactoryPayment(warehouseId, request));
    }
}
