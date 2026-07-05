package kg.bdc.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "dispatch_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DispatchItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id")
    private Dispatch dispatch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_uuid")
    private UUID productUuid;

    @Column(name = "quantity_boxes")
    private Integer quantityBoxes;

    @Column(name = "quantity_pieces")
    private Integer quantityPieces;

    private BigDecimal price;
}
