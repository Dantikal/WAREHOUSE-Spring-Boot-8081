package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "WS событие: обновление дашборда")
public class DashboardUpdateEvent {
    @Schema(example = "DASHBOARD_UPDATED") private String type;
}
