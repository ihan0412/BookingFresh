package est.oremi.backend12.bookingfresh.domain.session;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.session.Service.AIMessageService;
import est.oremi.backend12.bookingfresh.domain.session.Service.AIRecommendationService;
import est.oremi.backend12.bookingfresh.domain.session.Service.AISessionService;
import est.oremi.backend12.bookingfresh.domain.session.dto.*;
import est.oremi.backend12.bookingfresh.domain.session.entity.AiRecommendation;
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
    private final AIRecommendationService aiRecommendationService;

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

    //메시지 전송
    @PostMapping("/messages")
    public ResponseEntity<AiMessageResponse> sendMessage(
            @AuthenticationPrincipal Consumer user,
            @RequestBody AiMessageRequest request
    ) {
        AiMessageResponse response = aiMessageService.handleUserMessage(user, request);
        return ResponseEntity.ok(response);
    }

    // 세션 내 메시지 목록 조회
    @GetMapping("/messages/{sessionId}")
    public ResponseEntity<List<AiMessageResponse>> getMessages(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal Consumer user
    ) {
        List<AiMessageResponse> responses = aiMessageService.getMessagesBySession(sessionId, user);
        return ResponseEntity.ok(responses);
    }

    //세션 삭제
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal Consumer user
    ) {
        aiSessionService.deleteSession(sessionId, user);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


    //AI 추천 상품 생성 API
    @PostMapping("/recommendations")
    public ResponseEntity<List<AiRecommendationResponse>> generateRecommendations(
            @AuthenticationPrincipal Consumer user,
            @RequestBody AiRecommendationRequest request
    ) {
        // 세션/메시지 조회
        Session session = aiSessionService.findByIdAndUser(request.getSessionId(), user);
        Message aiMsg = aiMessageService.findById(request.getMessageId());
        AiResponseData aiResponse = new AiResponseData(
                aiMsg.getIntent().name(),        // or aiMsg.getResponseType()
                aiMsg.getStructuredJson(),       // parseRecipe 결과 JSON
                aiMsg.getContent()               // 원본 AI 텍스트
        );

        // 추천 생성
        List<AiRecommendation> recommendations =
                aiRecommendationService.generateRecommendations(session, aiMsg, aiResponse);

        // DTO 변환
        List<AiRecommendationResponse> responses = recommendations.stream()
                .map(AiRecommendationResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

}
