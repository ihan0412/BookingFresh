package est.oremi.backend12.bookingfresh.domain.mail;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.order.Order;
import est.oremi.backend12.bookingfresh.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class MailScheduler {
    private final OrderRepository orderRepository;
    private final MailService mailService;

    /**
     * 매일 오전 10시에 다음날 배송 예정인 주문 고객에게 리마인더 메일 발송
     */
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    public void sendDeliveryReminderMails() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Fetch join으로 consumer까지 한 번에 조회
        List<Order> tomorrowOrders = orderRepository.findByDeliveryDateWithConsumer(tomorrow);
        log.info("=== 배송 리마인더 대상 주문 수: {} ===", tomorrowOrders.size());

        int successCount = 0;
        for (Order order : tomorrowOrders) {
            try {
                Consumer consumer = order.getConsumer(); // 이미 fetch join으로 로드됨
                Order.DeliverySlot slot = order.getDeliverySlot();

                String timeDescription = switch (slot) {
                    case DAWN -> "새벽 배송";
                    case MORNING -> "오전 배송";
                    case AFTERNOON -> "오후 배송";
                    case NIGHT -> "밤 배송";
                };

                mailService.sendDeliveryReminderMail(
                        consumer.getEmail(),
                        consumer.getNickname(),
                        consumer.getId(),
                        order.getId(),
                        tomorrow,
                        timeDescription
                );

                successCount++;
            } catch (Exception e) {
                log.error("[배송 리마인더 발송 실패] orderId={}", order.getId(), e);
            }
        }

        log.info("=== 배송 리마인더 발송 완료 (성공: {}, 실패: {}) ===",
                successCount, tomorrowOrders.size() - successCount);
    }

}
