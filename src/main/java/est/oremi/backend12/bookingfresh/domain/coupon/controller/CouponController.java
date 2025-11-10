package est.oremi.backend12.bookingfresh.domain.coupon.controller;

import com.sun.security.auth.UserPrincipal;
import est.oremi.backend12.bookingfresh.domain.coupon.Coupon;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.CouponRegistrationRequest;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.CouponResponse;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.UserCouponProductResponse;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.UserCouponResponse;
import est.oremi.backend12.bookingfresh.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // 새로운 쿠폰을 등록하고 사용자에게 비동기 발급
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerCoupon(@RequestBody CouponRegistrationRequest request) {

        try {
            Coupon newCoupon = couponService.registerNewCoupon(request);

            Map<String, Object> response = new HashMap<>();
            response.put("id", newCoupon.getId());
            response.put("code", newCoupon.getCode());
            response.put("message", "쿠폰이 성공적으로 등록되었으며, 모든 사용자에게 비동기적으로 발급 중입니다.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // 카테고리 ID 오류 등 유효성 검사 실패 시
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "요청 오류");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // 쿠폰과 쿠폰이 적용 가능한 모든 카테고리 조회
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        List<CouponResponse> response = couponService.findAllCouponsWithCategories();
        // 모든 쿠폰과 그에 따른 카테고리 매핑 정보를 반환합니다.
        return ResponseEntity.ok(response);
    }

    // 사용자가 소유하고 사용가능한 모든 쿠폰 조회
    @GetMapping("/consumer/{consumerId}")
    public ResponseEntity<List<UserCouponResponse>> getAvailableUserCoupons(@PathVariable Long consumerId) {
        // Todo: 실제로는 인증 메커니즘을 통해 consumerId를 가져와야 합니다.
        if (consumerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<UserCouponResponse> response = couponService.findAvailableUserCoupons(consumerId);

        return ResponseEntity.ok(response);
    }

/*    // 상품에 사용 가능한, 사용자가 소유하고 있는 쿠폰 조회
    @GetMapping("/available/{productId}/consumer/{consumerId}")
    public ResponseEntity<List<UserCouponResponse>> getApplicableUserCoupons(
            @PathVariable Long productId,
            @PathVariable Long consumerId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        if (productId == null || consumerId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<UserCouponResponse> response = couponService.findApplicableCouponsForUserAndProduct(consumerId, productId);
        // 해당 사용자가 소유하고, 해당 상품에 적용 가능한 쿠폰 목록을 반환
        // 여기서 response 정렬하고 가자.
        return ResponseEntity.ok(response);
    }*/

    // 사용자가 사용 가능한, 소유하고 있는, 할인금액을 반영한 쿠폰 리스트
    @GetMapping("/available/{productId}/consumer/{consumerId}/prices")
    // ⭐ 반환 타입을 UserCouponProductResponse 리스트로 변경합니다.
    public ResponseEntity<List<UserCouponProductResponse>> getApplicableUserCouponsWithPrice(
            @PathVariable Long productId,
            @PathVariable Long consumerId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        // Todo: 실제로는 인증 메커니즘을 통해 consumerId를 가져와야 합니다. PathValuable 말고
        if (productId == null || consumerId == null) {
            return ResponseEntity.badRequest().build();
        }
        // ⭐ Service 호출은 그대로 유지 (매개변수가 consumerId, productId 2개로 통일됨)
        List<UserCouponProductResponse> response = couponService.findApplicableCouponsForUserAndProductWithPrice(consumerId, productId);

        // 해당 사용자가 소유하고, 해당 상품에 적용 가능한 쿠폰 목록 (할인 금액 포함 및 정렬됨) 반환
        return ResponseEntity.ok(response);
    }
}