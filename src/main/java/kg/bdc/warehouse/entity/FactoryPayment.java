package kg.bdc.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "factory_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactoryPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    private BigDecimal amount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    private String comment;

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }
}
