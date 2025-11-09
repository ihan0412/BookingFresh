package est.oremi.backend12.bookingfresh.domain.session.Service;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.session.AlanApiClient;
import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import est.oremi.backend12.bookingfresh.domain.session.entity.Session;
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
    private final OpenAiService openAiService;

    // 세션 생성
    public Session createSession(Consumer user) {
        Session session = Session.builder()
                .user(user)
                .title("새 AI 대화") // 임시 기본값
                .status(Session.SessionStatus.ACTIVE)
                .startedAt(LocalDateTime.now())
                .lastMessageAt(LocalDateTime.now())
                .build();

        Session saved = sessionRepository.save(session);

        // 시작 메시지 자동 생성
        Message systemMsg = Message.builder()
                .session(saved)
                .senderType(Message.SenderType.AI)
                .type(Message.MessageType.SYSTEM)
                .content("안녕하세요 😊 요리 도우미입니다. 어떤 걸 도와드릴까요?")
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(systemMsg);

        return saved;
    }

    public void handlePostMessage(Session session, Message userMessage) {
        // 세션의 첫 메시지일 경우에만
        if (session.getMessages().size() == 1 || session.getTitle() == null) {
            String title = openAiService.generateTitleFromMessage(userMessage.getContent());
            session.setTitle(title);
            sessionRepository.save(session);
        }

        session.updateLastMessageAt(LocalDateTime.now());
    }

}
