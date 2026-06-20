package kg.bdc.warehouse.repository;

import kg.bdc.warehouse.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(d.debt), 0) FROM Driver d WHERE d.status = 'ACTIVE'")
    BigDecimal sumAllDebts();
}
