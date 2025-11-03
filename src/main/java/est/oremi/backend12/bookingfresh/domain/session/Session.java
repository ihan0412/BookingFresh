package est.oremi.backend12.bookingfresh.domain.session;

import est.oremi.backend12.bookingfresh.domain.consumer.Consumer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private Consumer user;

    @Column(length = 100)
    private String title;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime lastMessageAt;

    // 연관 관계
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AiRecommendation> recommendations = new ArrayList<>();
}