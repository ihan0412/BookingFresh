package est.oremi.backend12.bookingfresh.domain.session.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiRecommendationRequest {
    private Long sessionId;   // 현재 대화 세션 ID
    private Long messageId;   // AI가 응답한 메시지 ID
}
