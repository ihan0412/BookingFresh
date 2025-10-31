package est.oremi.backend12.bookingfresh.domain.coupon;

import jakarta.persistence.*;

@Entity
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_code", nullable = false)
    private String code;

    @Column(name = "coupon_name", nullable = false)
    private String name;

    @Column(name = "discount_type", nullable = false)
    private String discountType;

    @Column(name = "discount_value", nullable = false)
    private String discountValue;

    @Column(name = "min_order_amount") // 쿠폰사용가능 최소금액
    private String minOrderAmount;

    @Column(name = "is_active") // 쿠폰사용가능 여부
    private String isActive;

    // 양방향이 필요 없는듯?

}
