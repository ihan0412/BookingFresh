package est.oremi.backend12.bookingfresh.domain.session.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiMessageRequest {
    private Long sessionId;
    private String content;
}
