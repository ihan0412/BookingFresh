package est.oremi.backend12.bookingfresh.domain.session.controller;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import est.oremi.backend12.bookingfresh.domain.session.Service.AIMessageService;
import est.oremi.backend12.bookingfresh.domain.session.Service.AIRecommendationService;
import est.oremi.backend12.bookingfresh.domain.session.Service.AISessionService;
import est.oremi.backend12.bookingfresh.domain.session.dto.*;
import est.oremi.backend12.bookingfresh.domain.session.entity.AiRecommendation;
import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import est.oremi.backend12.bookingfresh.domain.session.entity.Session;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(
        name = "AI 서비스 API",
        description = "BooKingFresh AI 세션,메시지,추천 api"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AIController {
    private final AISessionService aiSessionService;
    private final AIMessageService aiMessageService;
    private final AIRecommendationService aiRecommendationService;

    /* -----------------------------------------------------
     * 공통 인증 필요 함수
     * ----------------------------------------------------- */
    private Consumer requireUser(CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getConsumer() == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return userDetails.getConsumer();
    }

    //    AI 세션 생성
    @PostMapping("/sessions")
    public ResponseEntity<AiSessionResponse> startNewSession(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        Session session = aiSessionService.createSession(user);
        Consumer user = requireUser(userDetails);

        Session session = aiSessionService.createSession(user);

        URI location = URI.create("/api/ai/sessions/" + session.getIdx());
        return ResponseEntity.created(location)
                .body(AiSessionResponse.from(session));
    }

    //세션 목록 조회
    @GetMapping("/sessions")
    public ResponseEntity<List<AiSessionResponse>> getSessions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        List<AiSessionResponse> sessions = aiSessionService.getUserSessions(user);
//        return ResponseEntity.ok(sessions);
        Consumer user = requireUser(userDetails);

        return ResponseEntity.ok(
                aiSessionService.getUserSessions(user)
        );
    }

    //단일 세션 조회
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<AiSessionResponse> getSessionDetail(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        AiSessionResponse response = aiSessionService.getSessionDetail(sessionId, user);
//        return ResponseEntity.ok(response);
        Consumer user = requireUser(userDetails);

        return ResponseEntity.ok(
                aiSessionService.getSessionDetail(sessionId, user)
        );
    }

    //메시지 전송
    @PostMapping("/messages")
    public ResponseEntity<AiMessageResponse> sendMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AiMessageRequest request
    ) {
//        AiMessageResponse response = aiMessageService.handleUserMessage(user, request);
//        return ResponseEntity.ok(response);
        Consumer user = requireUser(userDetails);

        return ResponseEntity.ok(
                aiMessageService.handleUserMessage(user, request)
        );
    }

    // 세션 내 메시지 목록 조회
    @GetMapping("/messages/{sessionId}")
    public ResponseEntity<List<AiMessageResponse>> getMessages(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        List<AiMessageResponse> responses = aiMessageService.getMessagesBySession(sessionId, user);
//        return ResponseEntity.ok(responses);
        Consumer user = requireUser(userDetails);

        return ResponseEntity.ok(
                aiMessageService.getMessagesBySession(sessionId, user)
        );
    }

    //세션 삭제
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        aiSessionService.deleteSession(sessionId, user);
        Consumer user = requireUser(userDetails);
        aiSessionService.deleteSession(sessionId, user);

        return ResponseEntity.noContent().build(); // 204 No Content
    }


    //AI 추천 상품 생성 API
    @PostMapping("/recommendations")
    public ResponseEntity<List<AiRecommendationResponse>> generateRecommendations(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AiRecommendationRequest request
    ) {
        Consumer user = requireUser(userDetails);

        // 세션/메시지 조회
//        Session session = aiSessionService.findByIdAndUser(request.getSessionId(), user);
        Session session = aiSessionService.findByIdAndUser(request.getSessionId(), user);
        Message aiMsg = aiMessageService.findById(request.getMessageId());
        AiResponseData aiResponse = new AiResponseData(
                aiMsg.getIntent().name(),
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

    // 세션 내 시스템 추천상품 목록 조회
    @GetMapping("/recommendations/{sessionId}")
    public ResponseEntity<List<AiRecommendationResponse>> getRecommendationsBySession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Consumer user = requireUser(userDetails);

        // 세션 검증
        Session session = aiSessionService.findByIdAndUser(sessionId, user);

        // 추천 목록 조회
        List<AiRecommendation> recommendations = aiRecommendationService.getRecommendationsBySession(session);

        // DTO 변환
        List<AiRecommendationResponse> responses = recommendations.stream()
                .map(AiRecommendationResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }


}
