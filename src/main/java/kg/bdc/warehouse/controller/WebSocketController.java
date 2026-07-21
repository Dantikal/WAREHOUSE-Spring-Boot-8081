package kg.bdc.warehouse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.bdc.warehouse.dto.*;
import kg.bdc.warehouse.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * WebSocket endpoints documentation + manual trigger endpoints for testing.
 *
 * Real connections:
 *   WS Endpoint: /ws on the current host (ws:// or wss://)
 *   Subscribe to /topic/dashboard/update
 *   Subscribe to /topic/inventory/low-stock
 *   Subscribe to /topic/dispatch/created
 *   Subscribe to /topic/orders/new
 */
@RestController
@RequestMapping("/api/warehouse/websocket")
@RequiredArgsConstructor
@Tag(name = "WebSocket", description = """
        ## Real-time уведомления через STOMP/WebSocket
        
        **Подключение:**
        ```
        /ws
        /ws/websocket  (без SockJS)
        ```
        
        **Топики для подписки:**
        | Топик | Описание |
        |-------|----------|
        | `/topic/dashboard/update` | Обновление дашборда (баланс, долги) |
        | `/topic/inventory/low-stock` | Товар с низким остатком |
        | `/topic/dispatch/created` | Новая выдача создана |
        | `/topic/orders/new` | Новая заявка водителя |
        
        **Тестирование в Swagger:** используйте кнопки ниже чтобы вручную отправить WS событие.
        """)
public class WebSocketController {

    private final WebSocketNotificationService wsService;

    @PostMapping("/trigger/dashboard-update")
    @Operation(
        summary = "▶ Отправить: обновление дашборда",
        description = "Тестовый триггер. Отправляет событие в топик `/topic/dashboard/update`\n\n" +
                      "**Формат сообщения:**\n```json\n{\"type\":\"DASHBOARD_UPDATED\"}\n```",
        responses = {
            @ApiResponse(responseCode = "200", description = "Событие отправлено",
                content = @Content(schema = @Schema(implementation = DashboardUpdateEvent.class)))
        }
    )
    public ResponseEntity<Map<String, String>> triggerDashboardUpdate() {
        wsService.notifyDashboardUpdated();
        return ResponseEntity.ok(Map.of(
                "sent_to", "/topic/dashboard/update",
                "message", "{\"type\":\"DASHBOARD_UPDATED\"}"
        ));
    }

    @PostMapping("/trigger/low-stock")
    @Operation(
        summary = "▶ Отправить: низкий остаток",
        description = "Тестовый триггер. Отправляет событие в топик `/topic/inventory/low-stock`\n\n" +
                      "**Формат сообщения:**\n```json\n{\"productId\":1,\"productName\":\"Ice Cream\"}\n```",
        responses = {
            @ApiResponse(responseCode = "200", description = "Событие отправлено",
                content = @Content(schema = @Schema(implementation = LowStockEvent.class)))
        }
    )
    public ResponseEntity<Map<String, String>> triggerLowStock(
            @RequestParam(defaultValue = "1") Long productId,
            @RequestParam(defaultValue = "Мороженое Ягодное") String productName) {
        wsService.notifyLowStock(productId, productName);
        return ResponseEntity.ok(Map.of(
                "sent_to", "/topic/inventory/low-stock",
                "message", "{\"productId\":" + productId + ",\"productName\":\"" + productName + "\"}"
        ));
    }

    @PostMapping("/trigger/dispatch-created")
    @Operation(
        summary = "▶ Отправить: новая выдача",
        description = "Тестовый триггер. Отправляет событие в топик `/topic/dispatch/created`\n\n" +
                      "**Формат сообщения:**\n```json\n{\"dispatchId\":10}\n```",
        responses = {
            @ApiResponse(responseCode = "200", description = "Событие отправлено",
                content = @Content(schema = @Schema(implementation = DispatchCreatedEvent.class)))
        }
    )
    public ResponseEntity<Map<String, String>> triggerDispatchCreated(
            @RequestParam(defaultValue = "10") Long dispatchId) {
        wsService.notifyDispatchCreated(dispatchId);
        return ResponseEntity.ok(Map.of(
                "sent_to", "/topic/dispatch/created",
                "message", "{\"dispatchId\":" + dispatchId + "}"
        ));
    }

    @PostMapping("/trigger/new-order")
    @Operation(
        summary = "▶ Отправить: новая заявка водителя",
        description = "Тестовый триггер. Отправляет событие в топик `/topic/orders/new`\n\n" +
                      "**Формат сообщения:**\n```json\n{\"orderId\":5}\n```",
        responses = {
            @ApiResponse(responseCode = "200", description = "Событие отправлено",
                content = @Content(schema = @Schema(implementation = NewOrderEvent.class)))
        }
    )
    public ResponseEntity<Map<String, String>> triggerNewOrder(
            @RequestParam(defaultValue = "5") Long orderId) {
        wsService.notifyNewOrder(orderId);
        return ResponseEntity.ok(Map.of(
                "sent_to", "/topic/orders/new",
                "message", "{\"orderId\":" + orderId + "}"
        ));
    }
}
