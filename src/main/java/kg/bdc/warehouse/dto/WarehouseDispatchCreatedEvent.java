package kg.bdc.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Redis событие: создана выдача")
public class WarehouseDispatchCreatedEvent {
    @Schema(example = "warehouse.dispatch.created") private String event;
    @Schema(example = "10") private Long dispatchId;
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000") private UUID orderId;
    @Schema(example = "4") private Long driverId;
    @Schema(example = "1") private Long warehouseId;
    @Schema(example = "CREATED") private String status;
    private LocalDateTime createdAt;
}
