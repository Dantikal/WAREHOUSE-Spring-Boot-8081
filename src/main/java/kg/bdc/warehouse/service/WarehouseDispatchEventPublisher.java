package kg.bdc.warehouse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.bdc.warehouse.dto.WarehouseDispatchCreatedEvent;
import kg.bdc.warehouse.entity.Dispatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseDispatchEventPublisher {

    public static final String DISPATCH_CREATED_CHANNEL = "warehouse.dispatch.created";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publishCreated(Dispatch dispatch) {
        WarehouseDispatchCreatedEvent event = WarehouseDispatchCreatedEvent.builder()
                .event(DISPATCH_CREATED_CHANNEL)
                .dispatchId(dispatch.getId())
                .orderId(dispatch.getOrderId())
                .driverId(dispatch.getDriver() != null ? dispatch.getDriver().getId() : null)
                .warehouseId(dispatch.getWarehouse() != null ? dispatch.getWarehouse().getId() : null)
                .status(dispatch.getStatus())
                .createdAt(LocalDateTime.now())
                .build();

        try {
            redisTemplate.convertAndSend(DISPATCH_CREATED_CHANNEL, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сериализовать событие создания выдачи", e);
        } catch (RuntimeException e) {
            log.error("Не удалось опубликовать Redis событие {} для dispatchId={}, orderId={}",
                    DISPATCH_CREATED_CHANNEL, dispatch.getId(), dispatch.getOrderId(), e);
        }
    }
}
