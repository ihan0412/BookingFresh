package est.oremi.backend12.bookingfresh.domain.product.dto;

import est.oremi.backend12.bookingfresh.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse {
  private Long id;
  private String name;
  private String weightPieces;
  private int price;
  private String photoUrl;
  private String categoryName;

  public static ProductResponse fromEntity(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getWeight_pieces(),
        product.getPrice().intValue(),
        product.getPhotoUrl(),
        product.getCategory().getCategoryName().name()
    );
  }
}
