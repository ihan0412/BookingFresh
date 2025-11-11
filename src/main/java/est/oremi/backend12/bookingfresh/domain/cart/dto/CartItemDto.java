package est.oremi.backend12.bookingfresh.domain.cart.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemDto {
  private Long productId;     // 상품 ID
  private String name;        // 상품 이름
  private String weightPieces;// 중량/개수 정보
  private int quantity;       // 담긴 수량
  private BigDecimal price;          // 단가
  private BigDecimal lineTotal;      // 단가 × 수량
  private String photoUrl;    // 상품 이미지 URL
}