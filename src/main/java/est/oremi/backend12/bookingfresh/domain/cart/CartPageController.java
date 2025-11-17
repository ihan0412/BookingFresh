package est.oremi.backend12.bookingfresh.domain.cart;

import est.oremi.backend12.bookingfresh.domain.cart.dto.CartDto;
import est.oremi.backend12.bookingfresh.domain.cart.dto.CartItemDto;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartPageController {

  private final CartService cartService;

  // 장바구니 페이지 렌더링
  @GetMapping
  public String showCartPage(Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    Long consumerId = customUserDetails.getId();
    CartDto cart = cartService.getCart(consumerId);

    model.addAttribute("cartItems", cart.getItems());
    model.addAttribute("totalPrice", cart.getTotalAmount());

    return "cart";
  }

  // 장바구니에 상품 추가
  @PostMapping("/add")
  public String addToCart(@RequestParam Long productId,
      @RequestParam int quantity,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {
    Long consumerId = customUserDetails.getId();
    cartService.addProductToCart(consumerId, productId, quantity);

    redirectAttributes.addFlashAttribute("cartMessage", "장바구니에 상품을 담았습니다.");
    return "redirect:/products/" + productId;
  }

  // 장바구니 수량 변경 (최종 반영)
  @PostMapping("/update/{itemId}")
  public String updateCartItem(@PathVariable Long itemId,
      @RequestParam int quantity,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {
    Long consumerId = customUserDetails.getId();
    cartService.updateQuantity(consumerId, itemId, quantity);

    redirectAttributes.addFlashAttribute("updateMessage", "상품 수량이 변경되었습니다.");
    return "redirect:/cart";
  }

  // 장바구니 상품 삭제
  @PostMapping("/delete/{itemId}")
  public String deleteCartItem(@PathVariable Long itemId,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {
    Long consumerId = customUserDetails.getId();
    cartService.removeProductFromCart(consumerId, itemId);

    redirectAttributes.addFlashAttribute("deleteMessage", "상품이 장바구니에서 삭제되었습니다.");
    return "redirect:/cart";
  }

  // 장바구니 비우기
  @PostMapping("/clear")
  public String clearCart(@AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {
    Long consumerId = customUserDetails.getId();
    cartService.clearCart(consumerId);

    redirectAttributes.addFlashAttribute("clearMessage", "장바구니를 모두 비웠습니다.");
    return "redirect:/cart";
  }
}


