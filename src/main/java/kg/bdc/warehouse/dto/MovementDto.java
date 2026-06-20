package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Движение товара")
public class MovementDto {
    private Long id;
    @Schema(example = "DISPATCH", allowableValues = {"DELIVERY","DISPATCH","RETURN","ADJUSTMENT"})
    private String movementType;
    @Schema(example = "Пломбир Ванильный 500г") private String productName;
    @Schema(example = "10") private Integer quantityBoxes;
    @Schema(example = "0")  private Integer quantityPieces;
    @Schema(example = "Выдача водителю Иванов") private String comment;
    private LocalDateTime createdAt;
}
