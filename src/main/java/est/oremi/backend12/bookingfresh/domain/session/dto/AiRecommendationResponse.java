package est.oremi.backend12.bookingfresh.domain.session.dto;

import est.oremi.backend12.bookingfresh.domain.session.entity.AiRecommendation;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AiRecommendationResponse {
    private Long id;
    private Long sessionId;
    private Long messageId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;

    public static AiRecommendationResponse from(AiRecommendation entity) {
        return AiRecommendationResponse.builder()
                .id(entity.getIdx())
                .sessionId(entity.getSession().getIdx())
                .messageId(entity.getMessage().getIdx())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}