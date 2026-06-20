package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Запрос на создание выдачи")
public class CreateDispatchRequest {
    @Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED) private Long warehouseId;
    @Schema(example = "4", requiredMode = Schema.RequiredMode.REQUIRED) private Long driverId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) private List<DispatchItemRequest> items;
}
