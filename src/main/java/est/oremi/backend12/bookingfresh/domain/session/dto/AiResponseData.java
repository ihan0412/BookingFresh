package est.oremi.backend12.bookingfresh.domain.session.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public record AiResponseData(String type, String json, String rawText) {}

//@Getter
//@Setter
//@AllArgsConstructor
//public class AiResponseData {
//    private final String type;
//    private final String json;
//    private final String rawText;
//}
