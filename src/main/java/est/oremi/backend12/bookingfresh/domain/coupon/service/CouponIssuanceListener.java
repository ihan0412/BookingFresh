package est.oremi.backend12.bookingfresh.domain.coupon.service;

import est.oremi.backend12.bookingfresh.domain.consumer.repository.ConsumerRepository;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.coupon.Coupon;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.NewCouponRegisteredEvent;
import est.oremi.backend12.bookingfresh.domain.coupon.UserCoupon;
import est.oremi.backend12.bookingfresh.domain.coupon.repository.UserCouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponIssuanceListener {

    private final ConsumerRepository consumerRepository;
    private final UserCouponRepository userCouponRepository;

    // @Async = 이 메서드를 별도의 스레드에서 비동기적으로 실행 -> 쿠폰이 발행되면 모든 사용자에게
    // 쿠폰을 발행해야 하는데, 이때 동기적으로 실행하면 문제 발생 가능
    @EventListener
    @Async
    @Transactional
    public void handleNewCouponRegistration(NewCouponRegisteredEvent event) {
        Coupon newCoupon = event.newCoupon();
        System.out.println("[CouponIssuance] 새로운 쿠폰 등록 이벤트 감지: " + newCoupon.getName());

        // 모든 기존 사용자 조회 (테스트 환경에서는 findAll 사용) -> 실제로는 페이징 또는 스트리밍을 이용한다고 함
        List<Consumer> allConsumers = consumerRepository.findAll();

        if (allConsumers.isEmpty()) {
            System.out.println("[CouponIssuance] 현재 등록된 사용자가 없어 발급을 건너뜁니다.");
            return;
        }

        // 대량 발급할 UserCoupon 리스트 생성
        List<UserCoupon> userCouponsToSave = allConsumers.stream()
                .map(consumer -> UserCoupon.builder()
                        .consumer(consumer)
                        .coupon(newCoupon)
                        .build())
                .toList();

        // 대량 삽입, 얘도 마찬가지로 페이징, 스트리밍 기법 사용해야함
        userCouponRepository.saveAll(userCouponsToSave);

        System.out.println("[CouponIssuance] 새 쿠폰 '" + newCoupon.getName() + "'이 " + allConsumers.size() + "명의 사용자에게 발급 완료되었습니다.");
    }
}