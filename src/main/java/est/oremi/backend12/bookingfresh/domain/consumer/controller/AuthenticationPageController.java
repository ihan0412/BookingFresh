package est.oremi.backend12.bookingfresh.domain.consumer.controller;

import est.oremi.backend12.bookingfresh.domain.consumer.dto.AddConsumerRequest;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.LoginRequest;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping
public class AuthenticationPageController {

    @GetMapping("/signup")
    public String signUpPage(Model model, HttpServletRequest request) { // request는 제거해도 무방
        model.addAttribute("consumerRequest", new AddConsumerRequest());
        // (수정) 쿠키 확인 없이 항상 false
        model.addAttribute("isLoggedIn", false);
        return "authentication/signup";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request) { // request는 제거해도 무방
        model.addAttribute("loginRequest", new LoginRequest());
        // (수정) 쿠키 확인 없이 항상 false
        model.addAttribute("isLoggedIn", false);
        return "authentication/login";
    }

    @GetMapping("/home") // (★ 이 부분이 빠져있었습니다 ★)
    public String homePage(HttpServletRequest request, Model model) {
        model.addAttribute("isLoggedIn", isLoggedIn(request)); // (정상)
        return "home";
    }

    // 2. 마이페이지 (/mypage)
    @GetMapping("/mypage")
    public String myPage(HttpServletRequest request, Model model) {

        // 1. RT 쿠키를 확인하여 로그인 상태를 가져옵니다.
        boolean loggedIn = isLoggedIn(request);

        // 2. (★핵심★) 로그인 되어있지 않다면
        if (!loggedIn) {
            return "redirect:/login"; // 3. 로그인 페이지로 리다이렉트
        }

        // 4. 로그인 되어있다면, 모델에 상태를 담고 페이지 렌더링
        model.addAttribute("isLoggedIn", true);
        return "mypage/mypage";
    }

    @GetMapping("/mypage/edit")
    public String mypageEdit(HttpServletRequest request, Model model) {

        boolean loggedIn = isLoggedIn(request);
        if (!loggedIn) {
            return "redirect:/login"; // (★) 리다이렉트
        }

        model.addAttribute("isLoggedIn", true);
        return "mypage/edit";
    }

    @GetMapping("/mypage/coupons")
    public String mypageCoupons(HttpServletRequest request, Model model) {

        boolean loggedIn = isLoggedIn(request);
        if (!loggedIn) {
            return "redirect:/login"; // (★) 리다이렉트
        }

        model.addAttribute("isLoggedIn", true);
        return "mypage/coupons";
    }

    @GetMapping("/mypage/wishlist")
    public String mypageWishlist(HttpServletRequest request, Model model) {

        boolean loggedIn = isLoggedIn(request);
        if (!loggedIn) {
            return "redirect:/login"; // (★) 리다이렉트
        }

        model.addAttribute("isLoggedIn", true);
        return "mypage/wishlist";
    }

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

}