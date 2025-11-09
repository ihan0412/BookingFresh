package est.oremi.backend12.bookingfresh.domain.session;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.session.Service.AIService;
import est.oremi.backend12.bookingfresh.domain.session.Service.AISessionService;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiMessageRequest;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiMessageResponse;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiSessionResponse;
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
    private final AIService aiService;

    @PostMapping("/sessions")
    public ResponseEntity<AiSessionResponse> startNewSession(
            @AuthenticationPrincipal Consumer user
    ) {
        Session session = aiSessionService.createSession(user);

        URI location = URI.create("/api/ai/sessions/" + session.getIdx());
        return ResponseEntity.created(location)
                .body(AiSessionResponse.from(session));
    }

//    @PatchMapping("/sessions/{id}/purpose")
//    public ResponseEntity<AiSessionResponse> setSessionPurpose(
//            @PathVariable Long id,
//            @RequestBody AiSessionPurposeRequest request
//    ) {
//        Session updated = aiSessionService.setSessionPurpose(id, request.getPurpose());
//        return ResponseEntity.ok(AiSessionResponse.from(updated));
//    }

    @PostMapping("/messages")
    public ResponseEntity<AiMessageResponse> sendMessage(
            @AuthenticationPrincipal Consumer user,
            @RequestBody AiMessageRequest request
    ) {
        AiMessageResponse response = aiService.handleUserMessage(user, request);
        return ResponseEntity.ok(response);
    }
}
