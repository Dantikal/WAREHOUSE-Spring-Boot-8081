package kg.bdc.warehouse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kg.bdc.warehouse.service.ReturnsService;
import kg.bdc.warehouse.service.WarehouseContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/warehouse/returns")
@RequiredArgsConstructor
@Tag(name = "Returns", description = "Возвраты через drivers-service")
public class ReturnsController {

    private final ReturnsService returnsService;
    private final WarehouseContextService warehouseContextService;

    @PostMapping
    @Operation(
        summary = "Создать возврат",
        description = "Форвардит запрос возврата в drivers-service, добавляя ID текущего склада.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ответ drivers-service"),
            @ApiResponse(responseCode = "502", description = "drivers-service недоступен")
        }
    )
    public ResponseEntity<Object> createReturn(
            HttpServletRequest request,
            @RequestBody(required = false) Map<String, Object> body) {
        Long warehouseId = warehouseContextService.requireWarehouseId(request);
        return returnsService.forwardReturn(warehouseId, body, request);
    }
}
