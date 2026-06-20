package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "WS событие: низкий остаток")
public class LowStockEvent {
    @Schema(example = "1")          private Long productId;
    @Schema(example = "Ice Cream")  private String productName;
}
