package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "WS событие: новая заявка водителя")
public class NewOrderEvent {
    @Schema(example = "5") private Long orderId;
}
