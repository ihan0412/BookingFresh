package est.oremi.backend12.bookingfresh.domain.session.Service;

import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.session.AiResponseFormatter;
import est.oremi.backend12.bookingfresh.domain.session.AlanApiClient;
import est.oremi.backend12.bookingfresh.domain.session.Message;
import est.oremi.backend12.bookingfresh.domain.session.Session;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final AlanApiClient alanApiClient;     // 앨런 API 호출용
    private final OpenAIClient openAiClient;
    private final AiResponseFormatter formatter;   // 목적별 구조화 로직

    @Value("${ai.alan.client-id}")
    private String alanClientId;

    @Transactional
    public AiMessageResponse handleUserMessage(Consumer user, AiMessageRequest req){
        Session session = sessionRepository.findById(req.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        // 1.사용자 메시지 intent 분석 + 메시지 엔티티로 저장
        Message.IntentType intent = getIntentFromMessage(req.getContent());
        Message userMsg = messageRepository.save(Message.builder()
                .session(session)
                .senderType(Message.SenderType.USER)
                .type(Message.MessageType.QUESTION)
                .intent(intent)
                .content(req.getContent())
                .createdAt(LocalDateTime.now())
                .build());

        // 2. 앨런 LLM API 호출
        String aiRawText = alanApiClient.askAlan(req.getContent(), alanClientId);

        // 3. 세션 목적 기반 구조화 (JSON)
        AiResponseData structured = formatter.format(intent, aiRawText);

        // 4. AI 응답 메시지 저장
        Message aiMsg = messageRepository.save(Message.builder()
                .session(session)
                .senderType(Message.SenderType.AI)
                .type(Message.MessageType.ANSWER)
                .content(aiRawText)
                .structuredJson(structured.json())
                .createdAt(LocalDateTime.now())
                .build());

        session.setLastMessageAt(LocalDateTime.now());
        sessionRepository.save(session);

        // 5. DTO 응답
        return AiMessageResponse.builder()
                .sessionId(session.getIdx())
                .userMessage(userMsg.getContent())
                .aiMessage(aiMsg.getContent())
                .structuredJson(aiMsg.getStructuredJson())
                .responseType(structured.type())
                .build();
    }

    private Message.IntentType getIntentFromMessage(String userMessage) {
        try {
            //프롬프트 구성
            String prompt = """
            다음 문장의 의도를 분류하세요.

            문장: %s
            """.formatted(userMessage);

            // 요청 파라미터 생성
            ChatCompletionSystemMessageParam systemMsg =
                    ChatCompletionSystemMessageParam.builder()
                            .content("""
                        너는 사용자의 요청을 분석해 의도를 분류하는 AI이다.
                        반드시 아래 ENUM 이름 중 하나만 출력해야 한다:
                        RECIPE_ASSISTANT, COOKING_IDEA, SHOPPING_ASSISTANT, GENERAL_CHAT.
                        그 외 설명이나 문장은 절대 출력 금지.
                        """)
                            .build();

            ChatCompletionUserMessageParam userMsg =
                    ChatCompletionUserMessageParam.builder()
                            .content(prompt)
                            .build();

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model("gpt-4o-mini")
                    .messages(List.of(
                            ChatCompletionMessageParam.ofSystem(systemMsg),
                            ChatCompletionMessageParam.ofUser(userMsg)
                    ))
                    .temperature(0.0)
//                    .maxTokens(10)
                    .build();

            // 요청 전송 및 응답 수신
            ChatCompletion completion = openAiClient.chat().completions().create(params);

            // 응답에서 텍스트 추출
            String intentName = completion.choices().get(0).message().content().orElse("")
                    .trim();

            // Enum 매핑
            return Message.IntentType.valueOf(intentName.toUpperCase());
        } catch (Exception e) {
            return Message.IntentType.GENERAL_CHAT;
        }
    }
}
