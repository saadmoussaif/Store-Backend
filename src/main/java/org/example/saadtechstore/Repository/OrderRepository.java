package org.example.saadtechstore.Repository;

import org.example.saadtechstore.Domain.Enum.OrderStatut;
import org.example.saadtechstore.Domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByCustomerId(String customerId, Pageable pageable);

    Page<Order> findByStatus(OrderStatut status, Pageable pageable);

    List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :from AND o.createdAt <= :to")
    List<Order> findByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'PAID'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatut status);

    Optional<Order> findByCmiOrderId(String cmiOrderId);
}
