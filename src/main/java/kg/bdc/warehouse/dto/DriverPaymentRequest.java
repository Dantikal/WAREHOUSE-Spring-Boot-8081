package kg.bdc.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Платеж водителя в кассу склада")
public class DriverPaymentRequest {
    @NotNull
    @Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long driverId;

    @NotNull
    @DecimalMin("0.01")
    @Schema(example = "15000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(example = "CASH", allowableValues = {"CASH", "TRANSFER"})
    private String paymentMethod;

    @Schema(example = "Платеж от водителя")
    private String comment;
}
