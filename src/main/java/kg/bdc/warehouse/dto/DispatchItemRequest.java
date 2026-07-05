package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.UUID;
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Позиция в заявке на выдачу")
public class DispatchItemRequest {
    @Schema(example = "17bf6a41-485d-4562-bdf5-dea2bd4284b7", requiredMode = Schema.RequiredMode.REQUIRED) private UUID productId;
    @Schema(example = "10", requiredMode = Schema.RequiredMode.REQUIRED) private Integer quantityBoxes;
    @Schema(example = "0") private Integer quantityPieces;
}
