package kg.bdc.warehouse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.bdc.warehouse.dto.*;
import kg.bdc.warehouse.service.CashboxService;
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
    public ResponseEntity<CashboxBalanceDto> getBalance(
            @Parameter(description = "ID склада", example = "1", required = true)
            @RequestParam("warehouse_id") Long warehouseId) {
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
    public ResponseEntity<List<CashboxTransactionDto>> getTransactions(
            @Parameter(description = "ID склада", example = "1", required = true)
            @RequestParam("warehouse_id") Long warehouseId) {
        return ResponseEntity.ok(cashboxService.getTransactions(warehouseId));
    }
}
