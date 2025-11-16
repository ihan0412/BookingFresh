package est.oremi.backend12.bookingfresh.domain.session.controller;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
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
    private final AIMessageService aiMessageService;

    @GetMapping
    public String aiMainPage(@AuthenticationPrincipal Consumer user, Model model) {

        // 사용자 세션 목록 불러오기
        model.addAttribute("sessions", aiSessionService.getUserSessions(user));

        // 프론트에서 사용할 consumerId 전달
        model.addAttribute("consumerId", user.getId());

        // 템플릿으로 이동 (resources/templates/ai/chat.html)
        return "ai/chat";
    }

    @PostMapping("/messages")
    public ResponseEntity<AiMessageResponse> sendMessage(
            @AuthenticationPrincipal Consumer user,
            @RequestBody AiMessageRequest request
    ) {
//         로컬 테스트용 더미 사용자
        if (user == null) {
            user = Consumer.builder()
                    .email("localuser@bookingfresh.dev")
                    .nickname("로컬테스터")
                    .build();
        }

        AiMessageResponse response = aiMessageService.handleUserMessage(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions")
    public List<AiSessionResponse> getMySessions(@AuthenticationPrincipal Consumer user) {
        if (user == null) {
            user = Consumer.builder()
                    .email("localuser@bookingfresh.dev")
                    .nickname("로컬테스터")
                    .build();
        }
        return aiSessionService.getUserSessions(user);
    }

}
