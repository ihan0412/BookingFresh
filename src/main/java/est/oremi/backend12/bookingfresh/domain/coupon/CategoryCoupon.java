package est.oremi.backend12.bookingfresh.domain.coupon;

import est.oremi.backend12.bookingfresh.domain.product.Category;
import jakarta.persistence.*;

@Entity
@Table(name = "category_coupons")
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
}
