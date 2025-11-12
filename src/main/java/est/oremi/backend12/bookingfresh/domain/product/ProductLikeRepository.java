package est.oremi.backend12.bookingfresh.domain.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
  Optional<ProductLike> findByConsumerIdAndProductId(Long consumerId, Long productId);

  // 특정 사용자가 좋아요한 상품 목록 조회
  @Query("SELECT pl.product FROM ProductLike pl WHERE pl.consumer.id = :consumerId")
  List<Product> findProductsByConsumerId(@Param("consumerId") Long consumerId);
}


