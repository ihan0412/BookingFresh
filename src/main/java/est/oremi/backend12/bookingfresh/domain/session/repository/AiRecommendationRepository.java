package est.oremi.backend12.bookingfresh.domain.session.repository;

import est.oremi.backend12.bookingfresh.domain.session.entity.AiRecommendation;
import est.oremi.backend12.bookingfresh.domain.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiRecommendationRepository extends JpaRepository<AiRecommendation,Long> {
    List<AiRecommendation> findBySession(Session session);
}
