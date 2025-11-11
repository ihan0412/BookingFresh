package est.oremi.backend12.bookingfresh.domain.coupon;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Boolean isActive;

    // 쿠폰의 적용 카테고리들을 검색하기 위한 양방향 매핑
    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<CategoryCoupon> categoryCoupons = new ArrayList<>();

    @Builder
    public Coupon(String code, String name, String discountType, String discountValue, String minOrderAmount, Boolean isActive) {
        this.code = code;
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.isActive = isActive;
    }
}
