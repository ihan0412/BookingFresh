package est.oremi.backend12.bookingfresh.domain.cart;


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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CartItem {

  @Id @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Cart cart;

  @ManyToOne(fetch = FetchType.LAZY)
  private Product product;

  private int quantity;

  public CartItem(Cart cart, Product product, int quantity) {
    this.cart = cart;
    this.product = product;
    this.quantity = quantity;
  }


  public void addQuantity(int amount) {
    this.quantity += amount;
  }


  public void updateQuantity(int newQuantity) {
    this.quantity = newQuantity;
  }

  // 장바구니 상품 당 쿠폰
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_coupon_idx", nullable = true)
  private UserCoupon userCoupon = null;

  public void updateUserCoupon(UserCoupon userCoupon) {
    this.userCoupon = userCoupon;
  }
}

