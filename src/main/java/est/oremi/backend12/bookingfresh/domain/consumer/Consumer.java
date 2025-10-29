package est.oremi.backend12.bookingfresh.domain.cosumer;

import est.oremi.backend12.bookingfresh.domain.cart.Cart;
import est.oremi.backend12.bookingfresh.domain.coupon.UserCoupon;
import est.oremi.backend12.bookingfresh.domain.order.Order;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="consumers")
public class Consumer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String email;
  private String password;
  private String consumerName;
  private String address;
  private String detail_address;
  private String UserRole;

  @OneToOne(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = true)
  private Cart cart;

  @OneToMany(mappedBy = "consumer")
  private List<Order> orders = new ArrayList<>();

  @OneToMany(mappedBy = "consumer")
  private List<UserCoupon> userCoupons = new ArrayList<>();
}
