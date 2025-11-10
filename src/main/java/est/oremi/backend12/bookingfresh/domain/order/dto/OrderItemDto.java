package est.oremi.backend12.bookingfresh.domain.order.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemDto {
  private Long productId;
  private String productName;
  private int quantity;
  private BigDecimal price;
}

