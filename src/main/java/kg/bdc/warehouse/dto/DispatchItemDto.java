package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Позиция выдачи")
public class DispatchItemDto {
    private Long id;
    private Long productId;
    @Schema(example = "Пломбир Ванильный 500г") private String productName;
    @Schema(example = "10")    private Integer quantityBoxes;
    @Schema(example = "0")     private Integer quantityPieces;
    @Schema(example = "95.00") private BigDecimal price;
}
