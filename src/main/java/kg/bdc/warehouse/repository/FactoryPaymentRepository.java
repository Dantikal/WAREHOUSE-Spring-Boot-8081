package kg.bdc.warehouse.repository;

import kg.bdc.warehouse.entity.FactoryPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactoryPaymentRepository extends JpaRepository<FactoryPayment, Long> {
}
