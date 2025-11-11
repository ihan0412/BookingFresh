package est.oremi.backend12.bookingfresh.domain.coupon.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponCartItemRequest {
    private Long cartItemId; // 쿠폰을 적용/해제할 주문 항목 ID
    private Long userCouponId; // 적용할 UserCoupon ID (해제 시 null 또는 0)
}