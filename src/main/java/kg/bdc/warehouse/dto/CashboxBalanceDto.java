package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Баланс кассы")
public class CashboxBalanceDto {
    @Schema(example = "180000.00") private BigDecimal balance;
}
