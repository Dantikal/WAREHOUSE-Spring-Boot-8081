package kg.bdc.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "driver")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Driver {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;
    @Column(name = "full_name")
    private String fullName;
    private String phone;
    @Column(name = "vehicle_number")
    private String vehicleNumber;
    private BigDecimal debt;
    private String status;
}
