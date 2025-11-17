package est.oremi.backend12.bookingfresh.domain.cart;

import est.oremi.backend12.bookingfresh.domain.cart.dto.CartDto;
import est.oremi.backend12.bookingfresh.domain.cart.dto.CartItemDto;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartPageController {

  // 로그인 여부 확인 메서드 (예시)
  private boolean isLoggedIn(HttpServletRequest request) {
    // RT 쿠키나 세션을 검사하는 로직 구현
    // 예: request.getCookies()에서 Refresh Token 확인
    // 또는 request.getSession(false) != null 체크
    return true; // 실제 구현에 맞게 수정
  }

  // 장바구니 페이지 렌더링
  @GetMapping
  public String showCartPage(HttpServletRequest request, Model model) {
    if (!isLoggedIn(request)) {
      return "redirect:/login"; // 로그인 안 된 경우 로그인 페이지로 유도
    }

    model.addAttribute("isLoggedIn", true);
    return "cart"; // SSR은 화면만 렌더링, 데이터는 JS가 API 호출로 가져옴
  }
}