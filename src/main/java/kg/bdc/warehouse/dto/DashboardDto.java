package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Данные дашборда")
public class DashboardDto {
    @Schema(example = "180000") private BigDecimal cashBalance;
    @Schema(example = "420000") private BigDecimal factoryDebt;
    @Schema(example = "780000") private BigDecimal totalDriversDebt;
    @Schema(example = "8")      private Long lowStockProducts;
    @Schema(example = "12")     private Long newOrders;
}
