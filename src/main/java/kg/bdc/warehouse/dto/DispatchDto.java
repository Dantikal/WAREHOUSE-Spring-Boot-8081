package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Выдача товара водителю")
public class DispatchDto {
    private Long id;
    private Long warehouseId;
    private Long driverId;
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000") private UUID orderId;
    @Schema(example = "Иванов Алексей Петрович") private String driverName;
    @Schema(example = "INV-20240616-103045")      private String invoiceNumber;
    @Schema(example = "47500.00") private BigDecimal totalAmount;
    private LocalDateTime dispatchDate;
    @Schema(example = "CREATED", allowableValues = {"CREATED","ISSUED","CANCELED"})
    private String status;
    private List<DispatchItemDto> items;
}
