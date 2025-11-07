package est.oremi.backend12.bookingfresh.domain.session.repository;

import est.oremi.backend12.bookingfresh.domain.session.AiRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRecommendationRepository extends JpaRepository<AiRecommendation,Long> {

}
