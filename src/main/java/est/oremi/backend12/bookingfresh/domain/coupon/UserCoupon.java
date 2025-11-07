package est.oremi.backend12.bookingfresh.domain.coupon;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "usercoupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_used")
    private Boolean isUsed = false; // 기본값 초기화

    @Column(name = "is_applied")
    private Boolean isApplied = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_idx", nullable = false)
    private Consumer consumer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_idx", nullable = false)
    private Coupon coupon; // Foreign Key 역할

    @Builder
    public UserCoupon(Consumer consumer, Coupon coupon) {
        this.consumer = consumer;
        this.coupon = coupon;
        this.isUsed = false;
        this.isApplied = false;
    }
}
