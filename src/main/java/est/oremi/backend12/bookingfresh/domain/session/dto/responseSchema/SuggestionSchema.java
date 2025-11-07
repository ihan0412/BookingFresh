package est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("AI 요리 아이디어 추천 결과")
public class SuggestionSchema {
    @JsonPropertyDescription("추천된 요리나 메뉴 이름 리스트")
    public List<String> menus;
}