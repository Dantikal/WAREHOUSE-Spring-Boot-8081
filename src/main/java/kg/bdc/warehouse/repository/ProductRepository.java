package kg.bdc.warehouse.repository;

import kg.bdc.warehouse.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByUuid(UUID uuid);
    
    @Query("SELECT p FROM Product p WHERE p.uuid = :uuid")
    Optional<Product> findProductByUuid(@Param("uuid") UUID uuid);
}
