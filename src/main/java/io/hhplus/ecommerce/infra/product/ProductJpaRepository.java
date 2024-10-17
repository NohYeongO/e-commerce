package io.hhplus.ecommerce.infra.product;

import io.hhplus.ecommerce.domain.entity.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId IN :productIds")
    List<Product> findAllById(List<Long> productIds);

    @Query("SELECT p FROM Product p " +
            "JOIN OrderDetail od ON p.productId = od.product.productId " +
            "JOIN Order o ON od.order.orderId = o.orderId " +
            "WHERE o.orderDate BETWEEN :startDate AND CURRENT_DATE " +
            "GROUP BY p.productId " +
            "ORDER BY SUM(od.quantity) DESC")
    List<Product> findTop5Product();
}
