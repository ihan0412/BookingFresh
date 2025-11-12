package est.oremi.backend12.bookingfresh.domain.coupon.repository;

import est.oremi.backend12.bookingfresh.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // 모든 활성 쿠폰을 조회하는 메서드 (회원가입 시 발급 로직에 필요)
    List<Coupon> findByIsActive(Boolean isActive);

    // N+1 해결을 위해 CategoryCoupon 및 Category를 Fetch Join
    @Query("SELECT DISTINCT c FROM Coupon c JOIN FETCH c.categoryCoupons cc JOIN FETCH cc.category cat")
    List<Coupon> findAllWithCategories();
}