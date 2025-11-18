package est.oremi.backend12.bookingfresh.domain.cart;

import est.oremi.backend12.bookingfresh.domain.cart.dto.CartDto;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @GetMapping
  public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal CustomUserDetails user) {
    Long consumerId = user.getId();
    CartDto cart = cartService.getCart(consumerId);
    return ResponseEntity.ok(cart);
  }

  @PostMapping("/add")
  public ResponseEntity<Void> addProductToCart(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long productId,
      @RequestParam int quantity) {
    Long consumerId = user.getId();
    cartService.addProductToCart(consumerId, productId, quantity);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/update")
  public ResponseEntity<Void> updateQuantity(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long productId,
      @RequestParam int quantity) {
    Long consumerId = user.getId();
    cartService.updateQuantity(consumerId, productId, quantity);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/remove")
  public ResponseEntity<Void> removeProductFromCart(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long productId) {
    Long consumerId = user.getId();
    cartService.removeProductFromCart(consumerId, productId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/clear")
  public ResponseEntity<Void> clearCart(@AuthenticationPrincipal CustomUserDetails user) {
    Long consumerId = user.getId();
    cartService.clearCart(consumerId);
    return ResponseEntity.ok().build();
  }
}

