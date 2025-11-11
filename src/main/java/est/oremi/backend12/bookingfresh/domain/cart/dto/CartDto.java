package est.oremi.backend12.bookingfresh.domain.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartDto {
  private List<CartItemDto> items; // 장바구니에 담긴 모든 상품 목록
  private int totalQuantity;       // 총 수량
  private BigDecimal totalAmount;         // 총 금액
}
