package est.oremi.backend12.bookingfresh.domain.coupon.repository;

import est.oremi.backend12.bookingfresh.domain.coupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    // Consumer 엔티티를 패치 조인(Fetch Join)하여 N+1 문제를 방지
    // 해당 UserCoupon이 참조하는 Coupon 정보도 함께 패치 조인
    @org.springframework.data.jpa.repository.Query("SELECT uc FROM UserCoupon uc JOIN FETCH uc.coupon WHERE uc.consumer.id = :consumerId")
    List<UserCoupon> findByConsumerIdWithCoupon(@org.springframework.data.repository.query.Param("consumerId") Long consumerId);
}