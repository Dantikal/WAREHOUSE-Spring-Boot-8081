package kg.bdc.warehouse.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Склад")
public class WarehouseDto {
    @Schema(example = "1") private Long id;
    @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") private UUID externalUuid;
    @Schema(example = "Bishkek Cold Warehouse #1") private String name;
    @Schema(example = "ул. Логвиненко 55, Бишкек") private String address;
}
