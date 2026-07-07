package com.emras.order.repository;
import com.emras.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.items
        WHERE o.id = :id
        """)
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.statusHistory
        WHERE o.id = :id
        """)
    Optional<Order> findByIdWithHistory(@Param("id") Long id);

    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.items
        WHERE o.id = :id AND o.userId = :userId
        """)
    Optional<Order> findByIdAndUserIdWithItems(
            @Param("id") Long id, @Param("userId") Long userId);
}