package est.oremi.backend12.bookingfresh.domain.session.dto;

import est.oremi.backend12.bookingfresh.domain.session.Session;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AiSessionResponse {
    private Long id;
    private String title;
    private String purpose;
    private String status;
    private String introMessage;
    private LocalDateTime startedAt;
    private LocalDateTime lastMessageAt;

    public static AiSessionResponse from(Session session) {
        return AiSessionResponse.builder()
                .id(session.getIdx())
                .title(session.getTitle())
                .status(session.getStatus().name())
                .introMessage(session.getIntroMessage())
                .startedAt(session.getStartedAt())
                .lastMessageAt(session.getLastMessageAt())
                .build();
    }
}
