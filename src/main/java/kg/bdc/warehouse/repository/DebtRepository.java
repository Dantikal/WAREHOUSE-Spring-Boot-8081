package kg.bdc.warehouse.repository;

import kg.bdc.warehouse.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface DebtRepository extends JpaRepository<Debt, Long> {

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Debt d WHERE d.warehouse.id = :warehouseId AND d.debtType = 'FACTORY_DEBT'")
    BigDecimal sumFactoryDebtByWarehouse(@Param("warehouseId") Long warehouseId);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Debt d WHERE d.debtType = 'FACTORY_DEBT'")
    BigDecimal sumAllFactoryDebt();

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Debt d WHERE d.debtType = 'DRIVER_DEBT'")
    BigDecimal sumAllDriverDebt();
}
