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
import java.util.List;

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
                .body(AiSessionResponse.from(session, intro));
    }

    //세션 목록 조회
    @GetMapping("/sessions")
    public ResponseEntity<List<AiSessionResponse>> getSessions(
            @AuthenticationPrincipal Consumer user
    ) {
        List<AiSessionResponse> sessions = aiSessionService.getUserSessions(user);
        return ResponseEntity.ok(sessions);
    }

    //단일 세션 조회
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<AiSessionResponse> getSessionDetail(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal Consumer user
    ) {
        AiSessionResponse response = aiSessionService.getSessionDetail(sessionId, user);
        return ResponseEntity.ok(response);
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
