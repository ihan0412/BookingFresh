package est.oremi.backend12.bookingfresh.domain.coupon.service;

import est.oremi.backend12.bookingfresh.domain.cart.CartItem;
import est.oremi.backend12.bookingfresh.domain.cart.CartItemRepository;
import est.oremi.backend12.bookingfresh.domain.coupon.UserCoupon;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.CouponCartItemRequest;
import est.oremi.backend12.bookingfresh.domain.coupon.repository.UserCouponRepository;
import est.oremi.backend12.bookingfresh.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartCouponService {
    private final CartItemRepository cartItemRepository;
    private final UserCouponRepository userCouponRepository;

    @Transactional
    public void toggleCartItemCouponApplication(CouponCartItemRequest request, Long consumerId) {
        Long cartItemId = request.getCartItemId();
        Long newUserCouponId = request.getUserCouponId();

        // CartItem 조회 및 소유자 확인
        CartItem cartItem = cartItemRepository.findByIdWithCart(cartItemId)
                .orElseThrow(() -> new NotFoundException("장바구니 항목 ID " + cartItemId + "를 찾을 수 없습니다."));

        if (!cartItem.getCart().getConsumer().getId().equals(consumerId)) {
            throw new SecurityException("본인의 장바구니 항목에만 쿠폰을 변경할 수 있습니다.");
        }

        // 이전에 선택되어 있던 쿠폰 롤백
        UserCoupon oldCoupon = cartItem.getUserCoupon();
        if (oldCoupon != null) {
            oldCoupon.updateIsApplied(false);
            cartItem.updateUserCoupon(null);
        }

        // 새 쿠폰 적용
        if (newUserCouponId != null && newUserCouponId > 0) {

            UserCoupon newCoupon = userCouponRepository.findById(newUserCouponId)
                    .orElseThrow(() -> new NotFoundException("사용자 쿠폰 ID " + newUserCouponId + "를 찾을 수 없습니다."));

            // 유효성 검사 (isUsed, isApplied 상태 확인)
            if (newCoupon.getIsUsed()) {
                throw new IllegalStateException("이미 사용 완료된 쿠폰입니다.");
            }
            if (newCoupon.getIsApplied()) {
                throw new IllegalStateException("이 쿠폰은 현재 다른 항목에 적용 중입니다.");
            }

            newCoupon.updateIsApplied(true);
            cartItem.updateUserCoupon(newCoupon);
        }
        // 상태 변경 자동 반영
    }
}
