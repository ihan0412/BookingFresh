package est.oremi.backend12.bookingfresh.domain.session.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import est.oremi.backend12.bookingfresh.domain.product.ProductRepository;
import est.oremi.backend12.bookingfresh.domain.session.dto.AiResponseData;
import est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema.RecipeSchema;
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
    public List<AiRecommendation> generateRecommendations(Session session, Message userMsg, AiResponseData aiResponse) {
        try {
            // ① AI 응답 JSON에서 RecipeSchema 역직렬화
            if (aiResponse.json() == null || aiResponse.json().isBlank())
                return List.of();
            RecipeSchema recipe = objectMapper.readValue(aiResponse.json(), RecipeSchema.class);
            if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty())
                return List.of();

            // ② 재료 이름 목록 추출, 정제
            List<String> ingredientNames = recipe.getIngredients().stream()
                    .map(RecipeSchema.Ingredient::getName)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .map(name -> name.replaceAll("[^가-힣a-zA-Z0-9]", "").toLowerCase())
                    .filter(s -> !s.isEmpty())
                    .toList();
            if (ingredientNames.isEmpty()) {
                return Collections.emptyList();
            }

            // ③ 상품 DB 검색 (이름 기반 매칭)
            List<Product> matchedProducts = productRepository.findByKeywords(ingredientNames)
                    .stream().distinct().toList();
            if (matchedProducts.isEmpty()) return List.of();

            // ④ 추천 엔티티 생성 및 저장
            List<AiRecommendation> recommendations = matchedProducts.stream()
                    .map(p -> AiRecommendation.builder()
                            .session(session)
                            .message(userMsg)
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
}
