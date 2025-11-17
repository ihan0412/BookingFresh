package est.oremi.backend12.bookingfresh.domain.session.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AIPageController {

    @GetMapping("/ai")
    public String aiChatPage(HttpServletRequest request, Model model) {
        // 1. RT 쿠키를 확인하여 로그인 상태를 가져옵니다.
        boolean loggedIn = isLoggedIn(request);

        // 2. (★핵심★) 로그인 되어있지 않다면
        if (!loggedIn) {
            return "redirect:/login"; // 3. 로그인 페이지로 리다이렉트
        }

        // 4. 로그인 되어있다면, 모델에 상태를 담고 페이지 렌더링
        model.addAttribute("isLoggedIn", true);
        return "ai/chat";
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