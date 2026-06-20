package kg.bdc.warehouse.repository;

import kg.bdc.warehouse.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByWarehouseId(Long warehouseId);

    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :warehouseId AND (i.quantityBoxes <= 5 OR (i.quantityBoxes = 0 AND i.quantityPieces <= 0))")
    List<Inventory> findLowStock(@Param("warehouseId") Long warehouseId);

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.warehouse.id = :warehouseId AND (i.quantityBoxes <= 5 OR (i.quantityBoxes = 0 AND i.quantityPieces <= 0))")
    Long countLowStock(@Param("warehouseId") Long warehouseId);

    @Query("SELECT COUNT(DISTINCT i.product.id) FROM Inventory i WHERE i.warehouse.id = :warehouseId")
    Long countProductsByWarehouse(@Param("warehouseId") Long warehouseId);

    @Query("SELECT COALESCE(SUM(i.quantityBoxes * p.driverPrice), 0) FROM Inventory i JOIN i.product p WHERE i.warehouse.id = :warehouseId")
    java.math.BigDecimal sumInventoryValue(@Param("warehouseId") Long warehouseId);
}
