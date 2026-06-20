package kg.bdc.warehouse.repository;

import kg.bdc.warehouse.entity.CashboxTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CashboxTransactionRepository extends JpaRepository<CashboxTransaction, Long> {
    List<CashboxTransaction> findByWarehouseIdOrderByCreatedAtDesc(Long warehouseId);
}
