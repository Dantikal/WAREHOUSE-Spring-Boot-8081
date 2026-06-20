package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Операция кассы")
public class CashboxTransactionDto {
    private Long id;
    @Schema(example = "DRIVER_PAYMENT", allowableValues = {"DRIVER_PAYMENT","FACTORY_PAYMENT","OTHER"})
    private String type;
    @Schema(example = "15000.00") private BigDecimal amount;
    @Schema(example = "CASH", allowableValues = {"CASH","TRANSFER"}) private String paymentMethod;
    @Schema(example = "Платеж от Иванова") private String comment;
    private LocalDateTime createdAt;
}
