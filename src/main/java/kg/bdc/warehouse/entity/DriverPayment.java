package kg.bdc.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    private BigDecimal amount;

    @Column(name = "payment_method")
    private String paymentMethod;

    private String comment;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }
}
