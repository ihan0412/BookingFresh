package est.oremi.backend12.bookingfresh.domain.session.controller;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import est.oremi.backend12.bookingfresh.domain.session.Service.AIMessageService;
import est.oremi.backend12.bookingfresh.domain.session.Service.AISessionService;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiMessageRequest;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiMessageResponse;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIPageController {
    private final AISessionService aiSessionService;

    private Consumer requireUser(CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getConsumer() == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return userDetails.getConsumer();
    }

    @GetMapping
    public String aiMainPage(@AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        // CustomUserDetails → Consumer 변환
        Consumer user = requireUser(userDetails);

        // 사용자 세션 목록 불러오기
        model.addAttribute("sessions", aiSessionService.getUserSessions(user));

        // 프론트에서 사용할 consumerId 전달
        model.addAttribute("consumerId", user.getId());

        // 템플릿으로 이동 (resources/templates/ai/chat.html)
        return "ai/chat";
    }

}
