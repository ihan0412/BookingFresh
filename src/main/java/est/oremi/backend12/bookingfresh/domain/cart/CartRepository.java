package est.oremi.backend12.bookingfresh.domain.cart;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {

  Optional<Cart> findByConsumerId(Long consumerId);
}
