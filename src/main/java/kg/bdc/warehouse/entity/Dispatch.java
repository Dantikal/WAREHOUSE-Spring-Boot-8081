package kg.bdc.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dispatch")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dispatch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "dispatch_date")
    private LocalDateTime dispatchDate;

    private String status;

    @OneToMany(mappedBy = "dispatch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DispatchItem> items;

    @PrePersist
    protected void onCreate() {
        dispatchDate = LocalDateTime.now();
        if (status == null) status = "CREATED";
    }
}
