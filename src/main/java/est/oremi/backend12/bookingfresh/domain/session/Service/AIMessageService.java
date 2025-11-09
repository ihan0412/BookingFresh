package est.oremi.backend12.bookingfresh.domain.session.Service;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.session.AlanApiClient;
import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import est.oremi.backend12.bookingfresh.domain.session.entity.Session;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiMessageRequest;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiMessageResponse;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiResponseData;
import est.oremi.backend12.bookingfresh.domain.session.repository.MessageRepository;
import est.oremi.backend12.bookingfresh.domain.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AIMessageService {
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final AlanApiClient alanApiClient;     // 앨런 API 호출용
    private final OpenAiService openAiService;
    private final AISessionService aiSessionService;

    @Value("${ai.alan.client-id}")
    private String alanClientId;

    @Transactional
    public AiMessageResponse handleUserMessage(Consumer user, AiMessageRequest req){
        Session session = sessionRepository.findById(req.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        // 1.사용자 메시지 intent 분석 + 메시지 엔티티로 저장
        Message.IntentType intent = openAiService.getIntentFromMessage(req.getContent());
        Message userMsg = messageRepository.save(Message.builder()
                .session(session)
                .senderType(Message.SenderType.USER)
                .type(Message.MessageType.QUESTION)
                .intent(intent)
                .content(req.getContent())
                .createdAt(LocalDateTime.now())
                .build());

        // 2. 첫 메시지라면 세션 타이틀 생성
        aiSessionService.handlePostMessage(session, userMsg);

        // 3. 앨런 LLM API 호출
        String aiRawText = alanApiClient.askAlan(req.getContent(), alanClientId);

        // 4. 세션 목적 기반 구조화 (JSON)
        AiResponseData structured = openAiService.formatAlanResponse(intent, aiRawText);

        // 5. AI 응답 메시지 저장
        Message aiMsg = messageRepository.save(Message.builder()
                .session(session)
                .senderType(Message.SenderType.AI)
                .type(Message.MessageType.ANSWER)
                .content(aiRawText)
                .structuredJson(structured.json())
                .createdAt(LocalDateTime.now())
                .build());

        session.updateLastMessageAt(LocalDateTime.now());
        sessionRepository.save(session);

        // 6. DTO 응답
        return AiMessageResponse.builder()
                .sessionId(session.getIdx())
                .messageId(aiMsg.getIdx())
                .userMessage(userMsg.getContent())
                .aiMessage(aiMsg.getContent())
                .structuredJson(aiMsg.getStructuredJson())
                .responseType(structured.type())
                .build();
    }


}
