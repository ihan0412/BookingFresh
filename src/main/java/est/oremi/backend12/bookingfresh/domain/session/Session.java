package est.oremi.backend12.bookingfresh.domain.session;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_sessions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private Consumer user;

    @Column(length = 100)
    private String title;

//    @Enumerated(EnumType.STRING)
//    @Column(length = 50)
//    private SessionPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private SessionStatus status;

    @Column(length = 200)
    private String introMessage;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime lastMessageAt;

    //최근 대화 요약 (LLM 대화 유지용)
    @Column(columnDefinition = "TEXT")
    private String contextSummary;

    // 연관 관계
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AiRecommendation> recommendations = new ArrayList<>();

//    public enum SessionPurpose {
//        UNDEFINED, RECIPE_ASSISTANT, COOKING_IDEA, SHOPPING_ASSISTANT, GENERAL_CHAT
//    }

    public enum SessionStatus {
        ACTIVE, ENDED
    }

    @PrePersist
    public void onCreate() {
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
    }

    public void updateLastMessageAt(LocalDateTime time) {
        this.lastMessageAt = time;
    }

    public void endSession() {
        this.status = SessionStatus.ENDED;
        this.endedAt = LocalDateTime.now();
    }
}