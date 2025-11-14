package est.oremi.backend12.bookingfresh.domain.order;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;


@Setter
@Entity
@Getter
@Table(name = "orders")
public class Order {
    public enum OrderStatus {
        PENDING,
        COMPLETED,
        CANCELLED
    }
    public enum DeliverySlot {
        DAWN,   // 새벽
        MORNING, // 오전
        AFTERNOON, // 오후
        NIGHT   // 밤
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private BigDecimal totalPrice; //물품 전체 가격

    private BigDecimal final_cost; // 쿠폰 적용 후 최종 가격

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Boolean isReservation;

    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    private DeliverySlot deliverySlot;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id")
    private Consumer consumer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
}
