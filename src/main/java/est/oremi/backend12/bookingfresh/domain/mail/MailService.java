package est.oremi.backend12.bookingfresh.domain.mail;

import est.oremi.backend12.bookingfresh.domain.consumer.Consumer;
import est.oremi.backend12.bookingfresh.domain.order.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final MailLogRepository mailLogRepository;

    //주문 확인 메일 발송
    @Async
    public void sendOrderConfirmationMail(Consumer consumer, Order order) {
        String title = "[BookingFresh] 주문이 접수되었습니다.";
        String content = String.format("""
                안녕하세요, %s님!

                주문이 정상적으로 접수되었습니다.
                주문번호: %s
                배송 예정일: %s

                감사합니다.
                """,
                consumer.getNickname(),
                order.getId(),
                order.getDeliveryDateTime()
        );

        // MailType.ORDER_CONFIRMATION 으로 메일 및 로그 처리
        sendAndLog(consumer, MailLog.MailType.ORDER_CONFIRMATION, title, content);
    }

    @Async
    public void sendDeliveryReminderMail(Consumer consumer, Order order) {
        String title = "[BookingFresh] 내일 배송 예정 안내";
        String content = String.format("""
                안녕하세요, %s님!

                내일(%s) 배송이 예정되어 있습니다.
                ( 주문번호: %s )
                배송 일정에 참고 부탁드립니다.

                감사합니다.
                """,
                consumer.getNickname(),
                order.getId(),
                order.getDeliveryDateTime()
        );

        // MailType.DELIVERY_REMINDER 로 발송
        sendAndLog(consumer, MailLog.MailType.DELIVERY_REMINDER, title, content);
    }


    //발송 및 로그 처리 공통 로직
    private void sendAndLog(Consumer consumer, MailLog.MailType type, String title, String content) {

        //MailLog 엔티티 생성 및 PENDING 저장
        MailLog mailLog = MailLog.builder()
                .consumer(consumer)
                .mailType(type)
                .title(title)
                .content(content)
                .sentAt(LocalDateTime.now())
                .status(MailLog.MailStatus.PENDING)
                .build();
        mailLogRepository.save(mailLog);

        try {
            // 메일 발송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(consumer.getEmail());
            message.setSubject(title);
            message.setText(content);

            mailSender.send(message);

            // 성공 시 상태 업데이트
            mailLog.setStatus(MailLog.MailStatus.SENT);
            log.info("[메일 발송 성공] 대상: {}, 제목: {}", consumer.getEmail(), title);

        } catch (Exception e) {
            // 실패 시 상태 FAILED 로 변경
            mailLog.setStatus(MailLog.MailStatus.FAILED);
            log.error("[메일 발송 실패] 대상: {}, 사유: {}", consumer.getEmail(), e.getMessage());
        } finally {
            mailLogRepository.save(mailLog);
        }
    }
}
