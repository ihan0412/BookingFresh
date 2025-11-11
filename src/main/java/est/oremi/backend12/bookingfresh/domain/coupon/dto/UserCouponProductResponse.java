package est.oremi.backend12.bookingfresh.domain.coupon.dto;

import est.oremi.backend12.bookingfresh.domain.coupon.Coupon;
import est.oremi.backend12.bookingfresh.domain.coupon.UserCoupon;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class UserCouponProductResponse {

    // 사용자-쿠폰 관계 정보
    private Long userCouponId; // UserCoupon 테이블의 ID
    private boolean isUsed;
    private boolean isApplied;

    // 쿠폰 기본 정보 (Coupon 엔티티에서 가져옴)
    private Long couponId;
    private String code;
    private String name;
    private String discountType;
    private String discountValue;
    private Boolean isActive;

    private String calculatedDiscountAmount; // 이 쿠폰으로 얻을 수 있는 실제 할인 금액
    private String finalPriceAfterDiscount;  // 할인 적용 후 최종 가격
    private String minOrderAmount;           // 쿠폰 사용 가능 최소 금액

    // 이 쿠폰이 적용되는 카테고리 목록
    private List<CategoryInfo> applicableCategories;

    public static UserCouponProductResponse from(
            UserCoupon userCoupon,
            List<CategoryInfo> categories,
            String calculatedDiscountAmount,
            String finalPriceAfterDiscount) {
        Coupon coupon = userCoupon.getCoupon();

        return UserCouponProductResponse.builder()
                .userCouponId(userCoupon.getId())
                .isUsed(userCoupon.getIsUsed())
                .isApplied(userCoupon.getIsApplied())

                .couponId(coupon.getId())
                .code(coupon.getCode())
                .name(coupon.getName())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .calculatedDiscountAmount(calculatedDiscountAmount)
                .finalPriceAfterDiscount(finalPriceAfterDiscount)
                .minOrderAmount(coupon.getMinOrderAmount())
                .isActive(coupon.getIsActive())

                .applicableCategories(categories)
                .build();
    }


}