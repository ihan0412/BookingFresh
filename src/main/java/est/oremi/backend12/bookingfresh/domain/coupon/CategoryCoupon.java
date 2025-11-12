package est.oremi.backend12.bookingfresh.domain.coupon;

import est.oremi.backend12.bookingfresh.domain.product.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "category_coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_idx", nullable = false)
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_idx", nullable = false)
    private Category category;

    @Builder
    public CategoryCoupon(Coupon coupon, Category category) {
        this.coupon = coupon;
        this.category = category;
    }
}
