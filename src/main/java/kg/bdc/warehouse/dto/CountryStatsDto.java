package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Статистика по стране")
public class CountryStatsDto {
    @Schema(example = "5")       private Long totalWarehouses;
    @Schema(example = "1200000") private BigDecimal totalFactoryDebt;
    @Schema(example = "980000")  private BigDecimal totalDriversDebt;
    @Schema(example = "580000")  private BigDecimal totalCashBalance;
}
