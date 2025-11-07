package est.oremi.backend12.bookingfresh.domain.product;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

  // 상품 ID로 상품을 조회할 때 Category 엔티티를 함께 로딩 (Fetch Join), FQCN는 import 문 생략 + 명시
  @org.springframework.data.jpa.repository.Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE p.id = :productId")
  Optional<Product> findByIdWithCategory(@org.springframework.data.repository.query.Param("productId") Long productId);
}

