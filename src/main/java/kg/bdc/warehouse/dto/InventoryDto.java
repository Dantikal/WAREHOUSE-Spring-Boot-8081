package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Остатки товара на складе")
public class InventoryDto {
    private Long id;
    private Long productId;
    @Schema(example = "Пломбир Ванильный 500г") private String productName;
    @Schema(example = "4600001") private String barcode;
    @Schema(example = "24") private Integer piecesPerBox;
    @Schema(example = "50") private Integer quantityBoxes;
    @Schema(example = "12") private Integer quantityPieces;
    @Schema(example = "95.00") private BigDecimal driverPrice;
    private LocalDateTime updatedAt;
}
