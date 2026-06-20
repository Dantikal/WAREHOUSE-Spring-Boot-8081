package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Запрос изменения статуса выдачи")
public class UpdateDispatchStatusRequest {
    @Schema(example = "ISSUED", allowableValues = {"CREATED","ISSUED","CANCELED"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;
}
