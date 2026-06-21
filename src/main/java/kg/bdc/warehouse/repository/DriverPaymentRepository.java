package kg.bdc.warehouse.repository;

import kg.bdc.warehouse.entity.DriverPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverPaymentRepository extends JpaRepository<DriverPayment, Long> {
}
