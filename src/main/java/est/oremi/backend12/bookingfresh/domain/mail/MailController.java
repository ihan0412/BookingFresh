package est.oremi.backend12.bookingfresh.domain.mail;

import est.oremi.backend12.bookingfresh.domain.consumer.Consumer;
import est.oremi.backend12.bookingfresh.domain.consumer.ConsumerRepository;
import est.oremi.backend12.bookingfresh.domain.order.Order;
import est.oremi.backend12.bookingfresh.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    private final ConsumerRepository consumerRepository;
    private final OrderRepository orderRepository;

    //주문 확인 메일 발송 테스트
    @GetMapping("/api/mail/confirm/{orderId}")
    public String sendOrderConfirm(@PathVariable Long orderId) {
        Order order = orderRepository.findByIdWithConsumer(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
        Consumer consumer = order.getConsumer();

        mailService.sendOrderConfirmationMail(consumer, order);
        return String.format("주문 확인 메일 발송 요청 완료 (주문번호: %d, 수신자: %s)", order.getId(), consumer.getEmail());
    }

    //배송 리마인더 메일 발송 테스트
    @GetMapping("/api/mail/reminder/{orderId}")
    public String sendDeliveryReminder(@PathVariable Long orderId) {
        Order order = orderRepository.findByIdWithConsumer(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
        Consumer consumer = order.getConsumer();

        mailService.sendDeliveryReminderMail(consumer, order);
        return String.format("배송 리마인더 메일 발송 요청 완료 (주문번호: %d, 수신자: %s)", order.getId(), consumer.getEmail());
    }

}
