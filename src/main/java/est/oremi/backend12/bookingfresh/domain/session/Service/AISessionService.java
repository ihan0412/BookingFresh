package est.oremi.backend12.bookingfresh.domain.session.Service;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.session.AlanApiClient;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiSessionResponse;
import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import est.oremi.backend12.bookingfresh.domain.session.entity.Session;
import est.oremi.backend12.bookingfresh.domain.session.repository.MessageRepository;
import est.oremi.backend12.bookingfresh.domain.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AISessionService {
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final OpenAiService openAiService;

    // 세션 생성
    public Session createSession(Consumer user) {

        Session newSession = Session.builder()
                .user(user)
                .title("새 AI 대화") // 임시 기본값
                .status(Session.SessionStatus.ACTIVE)
                .startedAt(LocalDateTime.now())
                .lastMessageAt(LocalDateTime.now())
                .context("")
                .build();

        return sessionRepository.save(newSession);
    }

    //세션의 첫 메시지 처리
    public void handlePostMessage(Session session, Message userMessage) {
        // 세션의 첫 메시지로 세션 제목 생성
        if (session.getMessages().size() == 1 || session.getTitle() == null) {
            String title = openAiService.generateTitleFromMessage(userMessage.getContent());
            session.setTitle(title);
            sessionRepository.save(session);
        }

        session.updateLastMessageAt(LocalDateTime.now());
    }

    // 세션 목록 조회
    public List<AiSessionResponse> getUserSessions(Consumer user) {
        return sessionRepository.findByUserOrderByLastMessageAtDesc(user)
                .stream()
                .map(AiSessionResponse::from)
                .toList();
    }

    // 단일 세션 조회
    public AiSessionResponse getSessionDetail(Long sessionId, Consumer user) {
        Session session = sessionRepository.findByIdxAndUser(sessionId, user)
                .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));
        return AiSessionResponse.from(session);
    }

    //세션 삭제
    @Transactional
    public void deleteSession(Long sessionId, Consumer user) {
        // 해당 세션이 로그인 사용자 소유인지 확인
        Session session = sessionRepository.findByIdxAndUser(sessionId, user)
                .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));
        sessionRepository.delete(session);
    }

    // 세션 컨텍스트 관리
    @Transactional
    public void updateSessionContext(Session session) {
        // 최근 N개 메시지 조회
        List<Message> messages = messageRepository
                .findTop5BySessionOrderByCreatedAtDesc(session);

        // role별 구분 포함한 문자열 병합
        StringBuilder sb = new StringBuilder();
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message m = messages.get(i);
            sb.append(m.getSenderType())
                    .append(": ")
                    .append(m.getContent())
                    .append("\n");
        }

        // 세션 엔티티의 contextSummary 갱신
        session.setContext(sb.toString());
        session.updateLastMessageAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    public Session findByIdAndUser(Long sessionId, Consumer user) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        // 세션 소유자 체크 (Consumer 엔티티 구조에 맞춰 비교)
        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("해당 세션에 대한 권한이 없습니다.");
        }

        return session;
    }
}
