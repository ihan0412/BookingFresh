package est.oremi.backend12.bookingfresh.domain.consumer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldErrorResponse {
    private final String field;
    private final String message;

    public FieldErrorResponse(String field, String message) {
        this.field = field;
        this.message = message;
    }
}