package kg.bdc.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "uuid", unique = true)
    private UUID uuid;
    
    private String barcode;
    private String name;
    @Column(name = "pieces_per_box")
    private Integer piecesPerBox;
    @Column(name = "expiration_date")
    private LocalDate expirationDate;
    @Column(name = "batch_number")
    private String batchNumber;
    @Column(name = "factory_price")
    private BigDecimal factoryPrice;
    @Column(name = "driver_price")
    private BigDecimal driverPrice;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
