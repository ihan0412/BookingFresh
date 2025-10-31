package est.oremi.backend12.bookingfresh.domain.coupon;

import jakarta.persistence.*;

@Entity
@Table(name = "category_coupons")
public class CategoryCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
