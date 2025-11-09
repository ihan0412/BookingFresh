package est.oremi.backend12.bookingfresh.domain.mail;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.order.Order;
import est.oremi.backend12.bookingfresh.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class MailScheduler {
    private final OrderRepository orderRepository;
    private final MailService mailService;

    //매일 오전 10시에 다음날 배송 예정인 주문 고객에게 리마인더 메일 발송
    @Scheduled(cron = "0 0 10 * * *")
    public void sendDeliveryReminderMails() {
        LocalDateTime startOfTomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime endOfTomorrow = startOfTomorrow.plusDays(1);

        List<Order> tomorrowOrders = orderRepository.findByDeliveryDateTimeBetween(startOfTomorrow, endOfTomorrow);
        log.info("=== 배송 리마인더 대상 주문 수: {} ===", tomorrowOrders.size());

        int successCount = 0;
        for (Order order : tomorrowOrders) {
            try {
                Consumer consumer = order.getConsumer(); // @Transactional 덕분에 LAZY 접근 안전

                mailService.sendDeliveryReminderMail(
                        consumer.getEmail(),
                        consumer.getNickname(),
                        consumer.getId(),
                        order.getId(),
                        order.getDeliveryDateTime()
                );

                successCount++;
            } catch (Exception e) {
                // 개별 예외 MailService 내부 log.error()로 기록된다고 가정
                log.error("[배송 리마인더 발송 실패] orderId={}, reason={}", order.getId(), e.getMessage());
            }
        }

        log.info("=== 배송 리마인더 발송 완료 (성공: {}, 실패: {}) ===", successCount, tomorrowOrders.size() - successCount);
    }

}
