package est.oremi.backend12.bookingfresh.domain.product;

import est.oremi.backend12.bookingfresh.domain.product.dto.ProductResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  // 전체 상품 페이징 조회
  public Page<ProductResponse> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable)
        .map(ProductResponse::fromEntity);
  }

  // 카테고리별 상품 페이징 조회
  public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
    return productRepository.findByCategoryId(categoryId, pageable)
        .map(ProductResponse::fromEntity);
  }

  public Page<ProductResponse> searchProductsByName(String keyword, Pageable pageable) {
    Page<Product> products = productRepository.findByNameContainingIgnoreCase(keyword, pageable);

    return products.map(ProductResponse::fromEntity);
  }
}

