package est.oremi.backend12.bookingfresh.domain.coupon.repository;

import est.oremi.backend12.bookingfresh.domain.coupon.CategoryCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {
    List<CategoryCoupon> findByCouponId(Long couponId);
}