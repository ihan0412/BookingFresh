package est.oremi.backend12.bookingfresh.domain.coupon.dto;

import est.oremi.backend12.bookingfresh.domain.coupon.Coupon;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CouponResponse {
    private Long id;
    private String code;
    private String name;
    private String discountType;
    private String discountValue;
    private String minOrderAmount;
    private Boolean isActive;

    // 이 쿠폰이 적용되는 카테고리 목록
    private List<CategoryInfo> applicableCategories;

    public static CouponResponse from(Coupon coupon, List<CategoryInfo> categories) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .name(coupon.getName())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .isActive(coupon.getIsActive())
                .applicableCategories(categories)
                .build();
    }
}