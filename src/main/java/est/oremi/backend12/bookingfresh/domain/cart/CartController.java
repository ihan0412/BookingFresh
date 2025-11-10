package est.oremi.backend12.bookingfresh.domain.cart;

import est.oremi.backend12.bookingfresh.domain.cart.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  // 장바구니 조회 - 엔드포인트 예시 /cart?consumerId=1
  @GetMapping
  public ResponseEntity<CartDto> getCart(@RequestParam Long consumerId) {
    CartDto cart = cartService.getCart(consumerId);
    return ResponseEntity.ok(cart);
  }

  // 장바구니에 상품 추가 - 엔드포인트 예시 /cart/add?consumerId=1&productId=1&quantity=2
  @PostMapping("/add")
  public ResponseEntity<Void> addProductToCart(
      @RequestParam Long consumerId,
      @RequestParam Long productId,
      @RequestParam int quantity) {
    cartService.addProductToCart(consumerId, productId, quantity);
    return ResponseEntity.ok().build();
  }

  // 장바구니 수량 변경 - 엔드포인트 예시 /cart/update?consumerId=1&productId=1&quantity=3
  @PatchMapping("/update")
  public ResponseEntity<Void> updateQuantity(
      @RequestParam Long consumerId,
      @RequestParam Long productId,
      @RequestParam int quantity) {
    cartService.updateQuantity(consumerId, productId, quantity);
    return ResponseEntity.ok().build();
  }

  // 장바구니에서 상품 제거 - 엔드포인트 예시 /cart/remove?consumerId=1&productId=1
  @DeleteMapping("/remove")
  public ResponseEntity<Void> removeProductFromCart(
      @RequestParam Long consumerId,
      @RequestParam Long productId) {
    cartService.removeProductFromCart(consumerId, productId);
    return ResponseEntity.ok().build();
  }

  // 장바구니 비우기 - 엔드포인트 예시 /cart/clear?consumerId=1
  @DeleteMapping("/clear")
  public ResponseEntity<Void> clearCart(@RequestParam Long consumerId) {
    cartService.clearCart(consumerId);
    return ResponseEntity.ok().build();
  }
}
