package est.oremi.backend12.bookingfresh.domain.coupon;

import jakarta.persistence.*;

@Entity
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_code")
    private String code;

    @Column(name = "coupon_name")
    private String name;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "discount_value")
    private String discountValue;

    @Column(name = "min_order_amount") // 쿠폰사용가능 최소금액
    private String minOrderAmount;

    @Column(name = "is_active") // 쿠폰사용가능 여부
    private String isActive;




}
