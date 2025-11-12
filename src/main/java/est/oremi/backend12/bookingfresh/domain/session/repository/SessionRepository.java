package est.oremi.backend12.bookingfresh.domain.session.repository;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session,Long> {
    // 현재 로그인한 사용자의 세션 목록 (최근 대화순)
    List<Session> findByUserOrderByLastMessageAtDesc(Consumer user);

    // 특정 사용자의 세션 중 하나만 조회 (보안 목적)
    Optional<Session> findByIdxAndUser(Long idx, Consumer user);
}
