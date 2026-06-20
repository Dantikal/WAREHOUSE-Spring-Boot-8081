package kg.bdc.warehouse.websocket;

import kg.bdc.warehouse.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyDashboardUpdated() {
        log.debug("WS → /topic/dashboard/update");
        messagingTemplate.convertAndSend("/topic/dashboard/update",
                new DashboardUpdateEvent("DASHBOARD_UPDATED"));
    }

    public void notifyLowStock(Long productId, String productName) {
        log.debug("WS → /topic/inventory/low-stock: {}", productName);
        messagingTemplate.convertAndSend("/topic/inventory/low-stock",
                new LowStockEvent(productId, productName));
    }

    public void notifyDispatchCreated(Long dispatchId) {
        log.debug("WS → /topic/dispatch/created: {}", dispatchId);
        messagingTemplate.convertAndSend("/topic/dispatch/created",
                new DispatchCreatedEvent(dispatchId));
    }

    public void notifyNewOrder(Long orderId) {
        log.debug("WS → /topic/orders/new: {}", orderId);
        messagingTemplate.convertAndSend("/topic/orders/new",
                new NewOrderEvent(orderId));
    }
}
