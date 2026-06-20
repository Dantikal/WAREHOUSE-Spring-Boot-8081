package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "WS событие: создана выдача")
public class DispatchCreatedEvent {
    @Schema(example = "10") private Long dispatchId;
}
