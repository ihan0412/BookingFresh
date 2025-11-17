package est.oremi.backend12.bookingfresh.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.consumer WHERE o.id = :orderId")
    Optional<Order> findByIdWithConsumer(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o JOIN FETCH o.consumer WHERE o.deliveryDate = :deliveryDate")
    List<Order> findByDeliveryDateWithConsumer(@Param("deliveryDate") LocalDate deliveryDate);

    @Query("SELECT o FROM Order o WHERE o.consumer.id = :consumerId ORDER BY o.createdAt DESC")
    List<Order> findByConsumerId(@Param("consumerId") Long consumerId);
}
