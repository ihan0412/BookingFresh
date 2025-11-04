package est.oremi.backend12.bookingfresh.domain.consumer.entity;

import est.oremi.backend12.bookingfresh.domain.cart.Cart;
import est.oremi.backend12.bookingfresh.domain.coupon.UserCoupon;
import est.oremi.backend12.bookingfresh.domain.order.Order;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name="consumers")
public class Consumer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password")
  private String password;

  @Column(name = "nickname")
  private String nickname;

  @Column(name = "address")
  private String address;

  @Column(name = "detail_address")
  private String detailAddress;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @OneToOne(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = true)
  private Cart cart;

  @OneToMany(mappedBy = "consumer")
  private List<Order> orders = new ArrayList<>();

  @OneToMany(mappedBy = "consumer")
  private List<UserCoupon> userCoupons = new ArrayList<>();

  @Builder
  public Consumer(String email, String password, String nickname, String address, String detailAddress, LocalDateTime createdAt) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.address = address;
    this.detailAddress = detailAddress;
    this.createdAt = createdAt;
  }

}
