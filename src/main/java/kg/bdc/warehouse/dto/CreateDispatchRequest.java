package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;
import java.util.UUID;
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Запрос на создание выдачи")
public class CreateDispatchRequest {
    @Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED) private Long warehouseId;
    @Schema(example = "4", requiredMode = Schema.RequiredMode.REQUIRED) private Long driverId;
    @NotNull
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED) private UUID orderId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) private List<DispatchItemRequest> items;
}
