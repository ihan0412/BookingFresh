package est.oremi.backend12.bookingfresh.domain.consumer.controller;

import est.oremi.backend12.bookingfresh.domain.consumer.dto.AddConsumerRequest;
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
        return "signup";
    }

    // TODO: 로그인 페이지, 홈 페이지 요청 메서드
    @GetMapping("/lonin")
    public String loninPage(Model model) {
        model.addAttribute("consumerRequest", new AddConsumerRequest());
        return "lonin";
    }
}