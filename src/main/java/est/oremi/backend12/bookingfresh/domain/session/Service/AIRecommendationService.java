package est.oremi.backend12.bookingfresh.domain.session.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import est.oremi.backend12.bookingfresh.domain.product.ProductRepository;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiResponseData;
import est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema.RecipeSchema;
import est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema.SimplifiedRecipeSchema;
import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import est.oremi.backend12.bookingfresh.domain.session.entity.Session;
import est.oremi.backend12.bookingfresh.domain.session.entity.AiRecommendation;
import est.oremi.backend12.bookingfresh.domain.session.repository.AiRecommendationRepository;
import est.oremi.backend12.bookingfresh.domain.product.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIRecommendationService {

    private final ProductRepository productRepository;
    private final AiRecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<AiRecommendation> generateRecommendations(Session session, Message aiMsg, AiResponseData aiResponse) {
        try {
            // ① LLM이 추출한 키워드 JSON 파싱
            if (aiResponse.json() == null || aiResponse.json().isBlank())
                return List.of();

            SimplifiedRecipeSchema schema =
                    objectMapper.readValue(aiResponse.json(), SimplifiedRecipeSchema.class);

            List<String> ingredientKeywords = schema.getIngredients();
            if (ingredientKeywords == null || ingredientKeywords.isEmpty()) {
                log.info("추천 불가: LLM 키워드 없음");
                return List.of();
            }

            log.info("LLM 재료 키워드 = {}", ingredientKeywords);

            // ② 상품 DB 검색
            List<Product> matchedProducts =
                    productRepository.findByKeywords(ingredientKeywords)
                            .stream().distinct().toList();

            if (matchedProducts.isEmpty()) {
                log.info("키워드 기반 매칭 상품 없음");
                return List.of();
            }

            // ③ 추천 엔티티 생성 및 저장
            List<AiRecommendation> recommendations = matchedProducts.stream()
                    .map(p -> AiRecommendation.builder()
                            .session(session)
                            .message(aiMsg)
                            .productId(p.getId())
                            .productName(p.getName())
                            .price(p.getPrice())
                            .imageUrl(p.getPhotoUrl())
                            .build())
                    .toList();

            recommendationRepository.saveAll(recommendations);
            return recommendations;

        } catch (Exception e) {
            log.error("Failed to generate recommendations for session {}: {}", session.getIdx(), e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    //세션 내 추천된 내역 조회
    @Transactional(readOnly = true)
    public List<AiRecommendation> getRecommendationsBySession(Session session) {
        return recommendationRepository.findBySession(session);
    }

}
