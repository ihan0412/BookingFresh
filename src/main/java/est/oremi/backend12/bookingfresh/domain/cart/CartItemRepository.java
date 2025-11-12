package est.oremi.backend12.bookingfresh.domain.cart;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByCartId(Long cartId);
  Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
  // 쿠폰 적용 전 사용자의 장바구니가 맞는지 확인
  @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.cart c WHERE ci.id = :cartItemId")
  Optional<CartItem> findByIdWithCart(Long cartItemId);
}
