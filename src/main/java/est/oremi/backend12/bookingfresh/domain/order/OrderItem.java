package est.oremi.backend12.bookingfresh.domain.order;

import est.oremi.backend12.bookingfresh.domain.coupon.UserCoupon;
import est.oremi.backend12.bookingfresh.domain.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Getter
@Table(name = "order_items")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int quantity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  // 쿠폰 상태 추척
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_coupon_idx", nullable = true)
  private UserCoupon userCoupon;

  public void updateUserCoupon(UserCoupon userCoupon) {
    this.userCoupon = userCoupon;
  }
}