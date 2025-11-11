package est.oremi.backend12.bookingfresh.domain.session.dto;

import est.oremi.backend12.bookingfresh.domain.session.entity.Session;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AiSessionResponse {
    private Long id;
    private String title;
    private String status;
    private String introMessage;
    private LocalDateTime startedAt;
    private LocalDateTime lastMessageAt;

    public static AiSessionResponse from(Session session,String intro) {
        return AiSessionResponse.builder()
                .id(session.getIdx())
                .title(session.getTitle())
                .status(session.getStatus().name())
                .introMessage("안녕하세요 😊 요리 도우미입니다. 무엇을 도와드릴까요?")
                .startedAt(session.getStartedAt())
                .lastMessageAt(session.getLastMessageAt())
                .build();
    }

    public static AiSessionResponse from(Session session) {
        return AiSessionResponse.builder()
                .id(session.getIdx())
                .title(session.getTitle())
                .status(session.getStatus().name())
                .startedAt(session.getStartedAt())
                .lastMessageAt(session.getLastMessageAt())
                .build();
    }

}
