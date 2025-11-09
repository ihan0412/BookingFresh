package est.oremi.backend12.bookingfresh.domain.session.repository;

import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long> {

}
