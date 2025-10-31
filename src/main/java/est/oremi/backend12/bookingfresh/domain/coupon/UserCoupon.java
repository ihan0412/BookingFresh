package est.oremi.backend12.bookingfresh.domain.coupon;

import est.oremi.backend12.bookingfresh.domain.consumer.Consumer;
import jakarta.persistence.*;

@Entity
@Table(name = "usercoupons")
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_used")
    private Boolean isUsed;

    @Column(name = "is_applied")
    private Boolean isApplied;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_idx", nullable = false)
    private Consumer consumer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_idx", nullable = false)
    private Coupon coupon; // Foreign Key 역할

}
