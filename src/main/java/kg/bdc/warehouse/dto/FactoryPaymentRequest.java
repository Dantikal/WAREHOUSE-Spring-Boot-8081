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
@Schema(description = "Платеж склада заводу")
public class FactoryPaymentRequest {
    @NotNull
    @DecimalMin("0.01")
    @Schema(example = "50000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(example = "TRANSFER", allowableValues = {"CASH", "TRANSFER"})
    private String paymentMethod;

    @Schema(example = "Оплата заводу за май")
    private String comment;
}
