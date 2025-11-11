package est.oremi.backend12.bookingfresh.domain.coupon.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponCartItemRequest {
    private Long cartItem; // 쿠폰을 적용/해제할 주문 항목 ID (실제로는 OrderItem ID가 아닌 CartItem ID가 와야 하지만, 현재 OrderService 구현 구조상 OrderItem ID를 사용하도록 가정)
    private Long userCouponId; // 적용할 UserCoupon ID (해제 시 null 또는 0)
}