package est.oremi.backend12.bookingfresh.domain.product;

import est.oremi.backend12.bookingfresh.domain.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  // 전체 상품 페이징 조회
  public Page<ProductResponse> findAll(Pageable pageable) {
    return productRepository.findAll(pageable)
        .map(ProductResponse::fromEntity);
  }
  // 상품 단건 조회
  public Product findById(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + productId));
  }

  // 카테고리별 상품 페이징 조회
  public Page<ProductResponse> searchProductsByCategory(Long categoryId, Pageable pageable) {
    return productRepository.findByCategoryId(categoryId, pageable)
        .map(ProductResponse::fromEntity);
  }


  // 검색어 기반 상품 조회
  public Page<ProductResponse> searchProductsByName(String keyword, Pageable pageable) {
    Page<Product> products = productRepository.findByNameContainingIgnoreCase(keyword, pageable);

    return products.map(ProductResponse::fromEntity);
  }
}

