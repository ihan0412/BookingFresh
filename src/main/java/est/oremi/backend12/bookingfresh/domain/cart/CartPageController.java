package est.oremi.backend12.bookingfresh.domain.cart;

import est.oremi.backend12.bookingfresh.domain.cart.dto.CartDto;
import est.oremi.backend12.bookingfresh.domain.cart.dto.CartItemDto;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
public class CartPageController {

  // 로그인 여부 확인 메서드 (예시)
  private boolean isLoggedIn(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  // 장바구니 페이지 렌더링
  @GetMapping
  public String showCartPage(HttpServletRequest request, Model model) {
    if (!isLoggedIn(request)) {
      return "redirect:/login";
    }
    model.addAttribute("isLoggedIn", true);
    return "cart"; // 화면만 렌더링
  }
}




