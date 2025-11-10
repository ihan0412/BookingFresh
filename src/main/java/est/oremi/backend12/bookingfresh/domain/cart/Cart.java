package est.oremi.backend12.bookingfresh.domain.cart;


import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.product.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

  @Id @GeneratedValue
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  private Consumer consumer;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartItem> items = new ArrayList<>();

  public Cart(Consumer consumer) {
    this.consumer = consumer;
  }

  // 장바구니에 상품 추가 메서드
  public CartItem addItem(Product product, int quantity) {
    CartItem item = new CartItem(this, product, quantity);
    this.items.add(item);
    return item;
  }

  //장바구니에서 상품 제거 메서드
  public void removeItem(CartItem item) {
    this.items.remove(item);
  }

  //장바구니 비우기 메서드
  public void clear() {
    this.items.clear();
  }

}

