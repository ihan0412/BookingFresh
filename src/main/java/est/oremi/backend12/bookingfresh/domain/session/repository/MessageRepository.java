package est.oremi.backend12.bookingfresh.domain.session.repository;

import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import est.oremi.backend12.bookingfresh.domain.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
    // 세션의 메시지를 생성 시간순으로 정렬하여 조회
    List<Message> findBySessionOrderByCreatedAtAsc(Session session);
}
