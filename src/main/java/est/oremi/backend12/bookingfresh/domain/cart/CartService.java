package est.oremi.backend12.bookingfresh.domain.cart;

import est.oremi.backend12.bookingfresh.domain.cart.dto.CartDto;
import est.oremi.backend12.bookingfresh.domain.cart.dto.CartItemDto;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.consumer.repository.ConsumerRepository;
import est.oremi.backend12.bookingfresh.domain.product.Product;
import est.oremi.backend12.bookingfresh.domain.product.ProductRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final ConsumerRepository consumerRepository;
  private final ProductRepository productRepository;
  private final CartItemRepository cartItemRepository;

  // 장바구니에 상품 추가
  @Transactional
  public void addProductToCart(Long consumerId, Long productId, int quantity) {
    Consumer consumer = consumerRepository.findById(consumerId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));

    Cart cart = cartRepository.findByConsumerId(consumerId)
        .orElseGet(() -> new Cart(consumer));

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

    CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
        .orElseGet(() -> cart.addItem(product, quantity));
    // 이미 있던 경우에는 수량 누적
    if (item.getId() != null) {
      item.addQuantity(quantity);
    }
    cartRepository.save(cart);
  }

  //장바구니 물품 수량 변경
  @Transactional
  public void updateQuantity(Long consumerId, Long productId, int quantity) {

    Cart cart = cartRepository.findByConsumerId(consumerId)
        .orElseThrow(() -> new IllegalArgumentException("장바구니 없음"));

    CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
        .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품 없음"));

    item.updateQuantity(quantity);
    cartRepository.save(cart);
  }


  // 장바구니에서 상품 제거
  @Transactional
  public void removeProductFromCart(Long consumerId, Long productId) {
    Cart cart = cartRepository.findByConsumerId(consumerId)
        .orElseThrow(() -> new IllegalArgumentException("장바구니 없음"));

    CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
        .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품 없음"));

    cart.removeItem(item);
    cartItemRepository.delete(item);
  }

  // 장바구니 조회
  @Transactional
  public CartDto getCart(Long consumerId) {
    Cart cart = cartRepository.findByConsumerId(consumerId)
        .orElseThrow(() -> new IllegalArgumentException("장바구니 없음"));

    List<CartItemDto> items = cart.getItems().stream()
        .map(item -> {
          Product p = item.getProduct();
          BigDecimal lineTotal = p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
          return new CartItemDto(
              p.getId(),
              p.getName(),
              p.getWeight_pieces(),
              item.getQuantity(),
              p.getPrice(),
              lineTotal,
              p.getPhotoUrl()
          );
        })
        .toList();

    int totalQuantity = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    BigDecimal totalAmount = items.stream()
        .map(CartItemDto::getLineTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new CartDto(items, totalQuantity, totalAmount);
  }

  //장바구니 비우기
  @Transactional
  public void clearCart(Long consumerId) {
    Cart cart = cartRepository.findByConsumerId(consumerId)
        .orElseThrow(() -> new IllegalArgumentException("장바구니 없음"));

    cart.clear();
    cartRepository.save(cart);
  }
}



