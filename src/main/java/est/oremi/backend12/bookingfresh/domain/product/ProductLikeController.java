package est.oremi.backend12.bookingfresh.domain.product;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import est.oremi.backend12.bookingfresh.domain.product.dto.ProductResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductLikeController {

  private final ProductLikeService productLikeService;

  // 좋아요 생성
  @PostMapping("/{productId}/like")
  public ResponseEntity<Void> likeProduct(
          @PathVariable Long productId,
          @AuthenticationPrincipal CustomUserDetails userDetails) { //  @AuthenticationPrincipal 사용
    Long consumerId = userDetails.getId(); // ID 추출
    productLikeService.likeProduct(consumerId, productId);
    return ResponseEntity.ok().build();
  }

  // 좋아요 삭제
  @DeleteMapping("/{productId}/like")
  public ResponseEntity<Void> unlikeProduct(
          @PathVariable Long productId,
          @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long consumerId = userDetails.getId();
    productLikeService.unlikeProduct(consumerId, productId);
    return ResponseEntity.ok().build();
  }

  // 좋아요 표시한 목록 조회
  @GetMapping("/likes")
  public ResponseEntity<List<ProductResponse>> getLikedProducts(
          @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long consumerId = userDetails.getId();

    List<Product> products = productLikeService.getLikedProducts(consumerId);
    List<ProductResponse> productResponses = products.stream()
            .map(ProductResponse::fromEntity)
            .toList();
    return ResponseEntity.ok(productResponses);
  }

}
