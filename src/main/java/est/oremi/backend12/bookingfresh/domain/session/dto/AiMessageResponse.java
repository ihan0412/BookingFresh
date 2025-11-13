package est.oremi.backend12.bookingfresh.domain.session.dto;

import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiMessageResponse {
    private Long sessionId;
    private Long messageId;
    private String userMessage;
    private String aiMessage;
    private String structuredJson; // 구조화된 응답
    private String intentType;      // RECIPE_ASSISTANT / SHOPPING_ASSISTANT / ...
    private String responseType;    // STRUCTURED 결과 타입: RECIPE / SHOPPING / TEXT

    public static AiMessageResponse from(Message message) {
        // USER → userMessage, AI → aiMessage 로 구분
        if (message.getSenderType() == Message.SenderType.USER) {
            return AiMessageResponse.builder()
                    .sessionId(message.getSession().getIdx())
                    .messageId(message.getIdx())
                    .userMessage(message.getContent())
                    .intentType(message.getIntent() != null ? message.getIntent().name() : null)
//                    .structuredJson(message.getStructuredJson())
//                    .responseType(message.getType() != null ? message.getType().name() : null)
                    .build();
        } else {
            return AiMessageResponse.builder()
                    .sessionId(message.getSession().getIdx())
                    .messageId(message.getIdx())
                    .aiMessage(message.getContent())
                    .structuredJson(message.getStructuredJson())
                    .responseType(message.getStructuredType() != null ? message.getStructuredType().name() : null)
                    .build();
        }
    }
}
