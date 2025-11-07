package est.oremi.backend12.bookingfresh.domain.session.Service;

import est.oremi.backend12.bookingfresh.domain.consumer.Consumer;
import est.oremi.backend12.bookingfresh.domain.session.Message;
import est.oremi.backend12.bookingfresh.domain.session.Session;
import est.oremi.backend12.bookingfresh.domain.session.repository.MessageRepository;
import est.oremi.backend12.bookingfresh.domain.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AISessionService {
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    // 세션 생성
    public Session createSession(Consumer user) {
        Session session = Session.builder()
                .user(user)
                .title("AI 대화") // 임시 기본값
                .purpose(Session.SessionPurpose.UNDEFINED)
                .status(Session.SessionStatus.ACTIVE)
                .introMessage("안녕하세요 😊 요리 도우미입니다. 어떤 걸 도와드릴까요?")
                .startedAt(LocalDateTime.now())
                .lastMessageAt(LocalDateTime.now())
                .build();

        Session saved = sessionRepository.save(session);

        // 시작 메시지 자동 생성
        Message systemMsg = Message.builder()
                .session(saved)
                .senderType(Message.SenderType.AI)
                .type(Message.MessageType.SYSTEM)
                .content(session.getIntroMessage())
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(systemMsg);

        return saved;
    }

//    // 세션 목적 설정
//    public Session setSessionPurpose(Long sessionId, Session.SessionPurpose purpose) {
//        Session session = sessionRepository.findById(sessionId)
//                .orElseThrow(() -> new IllegalArgumentException("세션이 존재하지 않습니다."));
//
//        session.setPurpose(purpose);
//        switch (purpose) {
//            case COOKING_IDEA  -> session.setTitle("🍽 메뉴 아이디어 세션");
//            case RECIPE_ASSISTANT -> session.setTitle("🍳 레시피 조력자 세션");
//            case GENERAL_CHAT -> session.setTitle("💬 자유 대화 세션");
//            default -> session.setTitle("AI 대화");
//        }
//
//        return sessionRepository.save(session);
//    }

}
