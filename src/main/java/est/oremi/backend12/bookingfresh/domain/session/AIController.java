package est.oremi.backend12.bookingfresh.domain.session;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.session.Service.AIMessageService;
import est.oremi.backend12.bookingfresh.domain.session.Service.AISessionService;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiMessageRequest;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiMessageResponse;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiSessionResponse;
import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import est.oremi.backend12.bookingfresh.domain.session.entity.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AIController {
    private final AISessionService aiSessionService;
    private final AIMessageService aiMessageService;

    @PostMapping("/sessions")
    public ResponseEntity<AiSessionResponse> startNewSession(
            @AuthenticationPrincipal Consumer user
    ) {
        Session session = aiSessionService.createSession(user);

        // 세션 생성 시 AI가 남긴 첫 system message를 찾아서 반환
        String intro = session.getMessages().stream()
                .filter(m -> m.getType() == Message.MessageType.SYSTEM)
                .findFirst()
                .map(Message::getContent)
                .orElse("안녕하세요 😊");

        URI location = URI.create("/api/ai/sessions/" + session.getIdx());
        return ResponseEntity.created(location)
                .body(AiSessionResponse.builder()
                        .id(session.getIdx())
                        .title(session.getTitle())
                        .status(session.getStatus().name())
                        .introMessage(intro)
                        .startedAt(session.getStartedAt())
                        .lastMessageAt(session.getLastMessageAt())
                        .build());
    }


    @PostMapping("/messages")
    public ResponseEntity<AiMessageResponse> sendMessage(
            @AuthenticationPrincipal Consumer user,
            @RequestBody AiMessageRequest request
    ) {
        AiMessageResponse response = aiMessageService.handleUserMessage(user, request);
        return ResponseEntity.ok(response);
    }
}
