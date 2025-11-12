package est.oremi.backend12.bookingfresh.domain.mail;

import est.oremi.backend12.bookingfresh.domain.order.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final MailLogRepository mailLogRepository;

    //주문 확인 메일 발송
    @Async
    public void sendOrderConfirmationMail(
            String email,
            String nickname,
            Long consumerId,
            Long orderId,
            LocalDate deliveryDate,
            Order.DeliverySlot deliverySlot
    ) {
        // 배송 시간대 문구 변환
        String slotDesc = switch (deliverySlot) {
            case DAWN -> "새벽 배송";
            case MORNING -> "오전 배송";
            case AFTERNOON -> "오후 배송";
            case NIGHT -> "밤 배송";
        };

        String title = "[BookingFresh] 주문이 접수되었습니다.";
        String content = String.format("""
            안녕하세요, %s님!

            주문이 정상적으로 접수되었습니다. 😊

            주문번호 : %s
            배송 예정일 : %s (%s)

            신선한 상품을 안전하게 배송해드리겠습니다.
            감사합니다.

            ────────────────
            BookingFresh 드림
            """,
                nickname,
                orderId,
                deliveryDate,
                slotDesc
        );

        // MailType.ORDER_CONFIRMATION 으로 로그 처리
        try {
            sendAndLog(email, consumerId, MailLog.MailType.ORDER_CONFIRMATION, title, content);
            log.info("[주문 확인 메일 발송 성공] consumerId={}, orderId={}", consumerId, orderId);
        } catch (Exception e) {
            log.error("[주문 확인 메일 발송 실패] consumerId={}, orderId={}", consumerId, orderId, e);
        }
    }

    @Async
    public void sendDeliveryReminderMail(
            String email,
            String nickname,
            Long consumerId,
            Long orderId,
            LocalDate deliveryDate,
            String deliverySlotDesc
    ) {
        String title = "[BookingFresh] 내일 배송 예정 안내";
        String content = String.format("""
            안녕하세요, %s님!

            내일(%s) %s이 예정되어 있습니다.
            (주문번호: %s)
            배송 일정을 참고 부탁드립니다.

            감사합니다.
            """,
                nickname,
                deliveryDate,
                deliverySlotDesc,
                orderId
        );

        sendAndLog(email, consumerId, MailLog.MailType.DELIVERY_REMINDER, title, content);
    }


    //발송 및 로그 처리 공통 로직
    private void sendAndLog(String email, Long consumerId, MailLog.MailType type, String title, String content) {

        //MailLog 엔티티 생성 및 PENDING 저장
        MailLog mailLog = MailLog.builder()
                .consumerId(consumerId)
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
            message.setTo(email);
            message.setSubject(title);
            message.setText(content);

            mailSender.send(message);

            // 성공 시 상태 업데이트
            mailLog.setStatus(MailLog.MailStatus.SENT);
            log.info("[메일 발송 성공] 대상: {}, 제목: {}", email, title);

        } catch (Exception e) {
            // 실패 시 상태 FAILED 로 변경
            mailLog.setStatus(MailLog.MailStatus.FAILED);
            log.error("[메일 발송 실패] 대상: {}, 사유: {}", email, e.getMessage());
        } finally {
            mailLogRepository.save(mailLog);
        }
    }
}
