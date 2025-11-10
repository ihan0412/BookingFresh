package est.oremi.backend12.bookingfresh.domain.cart;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByCartId(Long cartId);
  Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
