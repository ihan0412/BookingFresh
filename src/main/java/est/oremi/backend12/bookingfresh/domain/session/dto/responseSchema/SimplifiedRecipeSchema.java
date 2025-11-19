package est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema;

import lombok.Getter;

import java.util.List;

@Getter
public class SimplifiedRecipeSchema {
    public List<String> ingredients;  // 재료 이름만 수집
}