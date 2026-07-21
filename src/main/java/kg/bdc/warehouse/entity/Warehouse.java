package kg.bdc.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "warehouse")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Warehouse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "external_uuid", nullable = false, unique = true)
    private UUID externalUuid;
    private String name;
    private String address;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (externalUuid == null) {
            externalUuid = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
    }
}
