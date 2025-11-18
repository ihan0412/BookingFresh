package est.oremi.backend12.bookingfresh.domain.order;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderPageController {


  private boolean isLoggedIn(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName())) {
          // 쿠키가 존재하면 true 반환
          return true;
        }
      }
    }
    return false;
  }

  // 주문 페이지 렌더링
  @GetMapping
  public String showOrderPage(HttpServletRequest request, Model model) {
    if (!isLoggedIn(request)) {
      return "redirect:/login"; // 로그인 안 된 경우 로그인 페이지로 유도
    }

    // SSR은 화면만 렌더링, 데이터는 JS가 API 호출로 가져옴
    model.addAttribute("isLoggedIn", true);
    return "order"; // order.html 템플릿 렌더링
  }

  @GetMapping("/{orderId}")
  public String showOrderPage(@PathVariable Long orderId,
      HttpServletRequest request,
      Model model) {
    if (!isLoggedIn(request)) {
      return "redirect:/login";
    }
    model.addAttribute("orderId", orderId);
    model.addAttribute("isLoggedIn", true);
    return "order"; // order.html
  }
}
