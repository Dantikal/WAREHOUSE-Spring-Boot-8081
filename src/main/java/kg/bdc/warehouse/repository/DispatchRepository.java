package kg.bdc.warehouse.repository;

import kg.bdc.warehouse.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    List<Dispatch> findByWarehouseId(Long warehouseId);

    List<Dispatch> findByWarehouseIdAndDriverId(Long warehouseId, Long driverId);

    List<Dispatch> findByDriverId(Long driverId);

    @Query("SELECT COUNT(d) FROM Dispatch d WHERE d.warehouse.id = :warehouseId AND d.status = 'CREATED'")
    Long countNewByWarehouse(@Param("warehouseId") Long warehouseId);
}
