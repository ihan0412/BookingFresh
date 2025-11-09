package est.oremi.backend12.bookingfresh.domain.session.dto;

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
}
