package est.oremi.backend12.bookingfresh.domain.mail;


import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mail_logs")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_idx", nullable = false)
    private Consumer consumer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MailType mailType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Column
    private MailStatus status;

    public enum MailType {
        ORDER_CONFIRMATION, DELIVERY_REMINDER, PROMOTION
    }

    public enum MailStatus {
        SENT, FAILED, PENDING
    }
}