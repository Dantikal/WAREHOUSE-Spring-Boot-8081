package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Статистика склада")
public class WarehouseStatsDto {
    @Schema(example = "120") private Long totalProducts;
    @Schema(example = "35")  private Long totalDrivers;
    @Schema(example = "2500000") private BigDecimal inventoryValue;
    @Schema(example = "180000")  private BigDecimal cashBalance;
    @Schema(example = "420000")  private BigDecimal factoryDebt;
}
