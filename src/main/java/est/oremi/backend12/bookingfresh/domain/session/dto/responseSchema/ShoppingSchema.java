package est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("쇼핑 어시스턴트 추천 결과")
public class ShoppingSchema {
    @JsonPropertyDescription("추천된 상품 목록")
    public List<Item> items;

    @JsonClassDescription("상품 정보")
    public static class Item {
        @JsonPropertyDescription("상품 이름")
        public String productName;

        @JsonPropertyDescription("상품 ID (쇼핑몰 내부 매핑용)")
        public int productId;
    }
}
