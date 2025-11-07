package est.oremi.backend12.bookingfresh.domain.session.dto;

import est.oremi.backend12.bookingfresh.domain.session.Session;
import lombok.Getter;

@Getter
public class AiSessionPurposeRequest {
    private Session.SessionPurpose purpose;
}