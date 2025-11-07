package est.oremi.backend12.bookingfresh.domain.coupon.dto;

import est.oremi.backend12.bookingfresh.domain.coupon.Coupon;

public record NewCouponRegisteredEvent(Coupon newCoupon) {}
// 데이터 전송 객체(DTO,VD)에 최적화된 클래스 문법?
// record 는 데이터 홀더의 역할을 수행하는 불변 클래스이다.