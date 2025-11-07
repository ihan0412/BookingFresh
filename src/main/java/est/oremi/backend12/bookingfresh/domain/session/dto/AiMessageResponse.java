package est.oremi.backend12.bookingfresh.domain.session.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiMessageResponse {
    private Long sessionId;
    private String userMessage;
    private String aiMessage;
    private String structuredJson; // 구조화된 응답
    private String responseType;
}
