package est.oremi.backend12.bookingfresh.domain.product;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.consumer.repository.ConsumerRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

  private final ProductLikeRepository productLikeRepository;
  private final ProductRepository productRepository;
  private final ConsumerRepository consumerRepository;


  // 좋아요 생성해서 사용자랑 상품 연결
  @Transactional
  public void likeProduct(Long consumerId, Long productId) {
    // 이미 좋아요 했는지 확인
    if (productLikeRepository.findByConsumerIdAndProductId(consumerId, productId).isPresent()) {
      throw new IllegalStateException("이미 좋아요한 상품입니다");
    }

    Consumer consumer = consumerRepository.findById(consumerId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

    ProductLike like = new ProductLike();
    like.setConsumer(consumer);
    like.setProduct(product);
    like.setLikedAt(LocalDateTime.now());

    productLikeRepository.save(like);
  }


  // 좋아요 삭제
  @Transactional
  public void unlikeProduct(Long consumerId, Long productId) {
    ProductLike like = productLikeRepository.findByConsumerIdAndProductId(consumerId, productId)
        .orElseThrow(() -> new IllegalStateException("좋아요하지 않은 상품입니다"));

    productLikeRepository.delete(like);
  }


  // 사용자가 좋아요 표시한 상품들 리스트 조회
  @Transactional
  public List<Product> getLikedProducts(Long consumerId) {
    return productLikeRepository.findProductsByConsumerId(consumerId);
  }
}


