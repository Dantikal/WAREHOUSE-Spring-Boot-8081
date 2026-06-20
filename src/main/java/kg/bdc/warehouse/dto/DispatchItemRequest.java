package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Позиция в заявке на выдачу")
public class DispatchItemRequest {
    @Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED) private Long productId;
    @Schema(example = "10", requiredMode = Schema.RequiredMode.REQUIRED) private Integer quantityBoxes;
    @Schema(example = "0") private Integer quantityPieces;
}
