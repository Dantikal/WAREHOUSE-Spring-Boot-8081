package kg.bdc.warehouse.repository;

import kg.bdc.warehouse.entity.Cashbox;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CashboxRepository extends JpaRepository<Cashbox, Long> {
    Optional<Cashbox> findByWarehouseId(Long warehouseId);
}
