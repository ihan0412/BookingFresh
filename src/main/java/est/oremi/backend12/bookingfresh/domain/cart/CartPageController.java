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
  public String showCartPage(Model model, Principal principal) {
    Long consumerId = Long.valueOf(principal.getName());
    CartDto cart = cartService.getCart(consumerId);

    model.addAttribute("cartItems", cart.getItems());
    model.addAttribute("totalPrice", cart.getTotalAmount());

    return "cart";
  }
  @PostMapping("/add")
  public String addToCart(@RequestParam Long productId,
      @RequestParam int quantity,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {
    Long consumerId = customUserDetails.getId();
    cartService.addProductToCart(consumerId, productId, quantity);

    // 메시지를 flash attribute로 전달
    redirectAttributes.addFlashAttribute("cartMessage", "장바구니에 상품을 담았습니다.");

    // ✅ 현재 페이지로 돌아가기 (예: 상품 상세 페이지)
    return "redirect:/products/" + productId;
  }
}

