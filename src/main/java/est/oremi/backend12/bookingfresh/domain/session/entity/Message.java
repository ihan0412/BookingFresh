package est.oremi.backend12.bookingfresh.domain.session.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_idx", nullable = false)
    private Session session;

    @Enumerated(EnumType.STRING)
    private SenderType senderType;

    @Column(columnDefinition = "TEXT")
    private String content;

    // AI 응답을 구조화한 JSON
    @Column(columnDefinition = "TEXT")
    private String structuredJson;

    //사용자의 의도 분석 결과
    @Enumerated(EnumType.STRING)
    private IntentType intent;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StructuredType structuredType;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public enum SenderType{
        USER, AI
    }
    public enum IntentType{
        RECIPE_ASSISTANT,
//        COOKING_IDEA,
        GENERAL_CHAT
    }
    public enum StructuredType {
        RECIPE, SUGGESTION, TEXT
    }
}
