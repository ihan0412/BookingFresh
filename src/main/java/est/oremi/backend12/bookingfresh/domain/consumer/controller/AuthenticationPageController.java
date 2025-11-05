package est.oremi.backend12.bookingfresh.domain.consumer.controller;

import est.oremi.backend12.bookingfresh.domain.consumer.dto.AddConsumerRequest;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.LoginRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping
public class AuthenticationPageController {

    @GetMapping("/signup")
    public String signUpPage(Model model) {
        model.addAttribute("consumerRequest", new AddConsumerRequest());
        return "authentication/signup";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "authentication/login";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home"; // src/main/resources/templates/home.html 파일명을 반환
    }
}