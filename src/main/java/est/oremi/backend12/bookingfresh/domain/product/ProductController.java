package est.oremi.backend12.bookingfresh.domain.product;

import est.oremi.backend12.bookingfresh.domain.product.dto.ProductResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  // 전체 상품 페이징 조회
  @GetMapping
  public Page<ProductResponse> getProducts(Pageable pageable) {
    return productService.getAllProducts(pageable);
  }

  // 카테고리별 상품 페이징 조회
  @GetMapping("/category/{categoryId}")
  public Page<ProductResponse> getProductsByCategory(
      @PathVariable Long categoryId,
      Pageable pageable) {
    return productService.getProductsByCategory(categoryId, pageable);
  }

  // 검색어 기반 상품 조회
  @GetMapping("/search")
  public ResponseEntity<Page<ProductResponse>> searchProducts(@RequestParam String keyword,
      Pageable pageable) {
    Page<ProductResponse> products = productService.searchProductsByName(keyword, pageable);
    return ResponseEntity.ok(products);
  }
}

