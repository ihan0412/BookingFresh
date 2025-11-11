package est.oremi.backend12.bookingfresh.domain.order;

import est.oremi.backend12.bookingfresh.domain.cart.Cart;
import est.oremi.backend12.bookingfresh.domain.cart.CartItem;
import est.oremi.backend12.bookingfresh.domain.cart.CartItemRepository;
import est.oremi.backend12.bookingfresh.domain.cart.CartRepository;
import est.oremi.backend12.bookingfresh.domain.coupon.Coupon;
import est.oremi.backend12.bookingfresh.domain.coupon.UserCoupon;
import est.oremi.backend12.bookingfresh.domain.coupon.service.CouponService;
import est.oremi.backend12.bookingfresh.domain.order.dto.OrderDto;
import est.oremi.backend12.bookingfresh.domain.product.Product;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final CartRepository cartRepository;
  private final OrderRepository orderRepository;
  private final CartItemRepository cartItemRepository;


  //주문 생성
  @Transactional
  public Long createOrder(Long consumerId, LocalDateTime deliveryDateTime, boolean isReservation) {
    Cart cart = cartRepository.findByConsumerId(consumerId)
        .orElseThrow(() -> new IllegalArgumentException("장바구니 없음"));

    if (cart.getItems().isEmpty()) {
      throw new IllegalStateException("장바구니가 비어있습니다");
    }

    Order order = new Order();
    order.setConsumer(cart.getConsumer());
    order.setCreatedAt(LocalDateTime.now());
    order.setDeliveryDateTime(deliveryDateTime);
    order.setIsReservation(isReservation);
    order.setStatus(Order.OrderStatus.PENDING);

    BigDecimal totalPrice = BigDecimal.ZERO;

    // 주문 총액 변수 (쿠폰 적용 전/후)
    BigDecimal totalOriginalPrice = BigDecimal.ZERO;
    BigDecimal finalTotalCost = BigDecimal.ZERO;
    List<UserCoupon> usedCoupons = new ArrayList<>();

    // 총액 계산 및 OrderItem 생성 (쿠폰 적용)
    for (CartItem cartItem : cart.getItems()) {
      Product product = cartItem.getProduct();
      int quantity = cartItem.getQuantity();
      BigDecimal itemOriginalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
      BigDecimal itemDiscountAmount = BigDecimal.ZERO;

      UserCoupon appliedCoupon = cartItem.getUserCoupon(); // CartItem에 연결된 예약 쿠폰 조회
      // 쿠폰 적용 로직
      if (appliedCoupon != null) {
        // 유효성 검증
        validateCouponForOrder(appliedCoupon, consumerId, product, itemOriginalPrice);
        // 할인 금액 계산
        itemDiscountAmount = calculateDiscountAmount(itemOriginalPrice, appliedCoupon.getCoupon());
        usedCoupons.add(appliedCoupon);
      }

      // 최종 가격 및 총합 계산
      BigDecimal itemFinalPrice = itemOriginalPrice.subtract(itemDiscountAmount);

      totalOriginalPrice = totalOriginalPrice.add(itemOriginalPrice);
      finalTotalCost = finalTotalCost.add(itemFinalPrice);

      // OrderItem 생성 및 연관관계 설정
      OrderItem orderItem = new OrderItem();
      orderItem.setOrder(order);
      orderItem.setProduct(product);
      orderItem.setQuantity(quantity);
      orderItem.setUserCoupon(appliedCoupon);

      order.getOrderItems().add(orderItem);
    }

    // 주문 엔티티의 최종 금액 설정
    order.setTotalPrice(totalOriginalPrice);
    order.setFinal_cost(finalTotalCost);

    orderRepository.save(order);

    // 사용된 쿠폰 상태 최종 변경 (isUsed = true, isApplied = false)
    for (UserCoupon uc : usedCoupons) {
      // UserCoupon.use() 메서드 호출- isUsed=true, isApplied=false로 최종 사용 처리
      uc.use(order.getId());
    }

    orderRepository.save(order);

    cart.clear(); // 주문 생성 후 장바구니 비우기
    cartRepository.save(cart);

    return order.getId();
  }
  // 주문 조회
  @Transactional
  public OrderDto getOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문"));

    return OrderDto.from(order);
  }

  // 주문 취소
  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문"));

    if (order.getStatus() == Order.OrderStatus.CANCELLED) {
      throw new IllegalStateException("이미 취소된 주문입니다");
    }

    // 쿠폰 상태 롤백
    order.getOrderItems().stream()
            .map(OrderItem::getUserCoupon)
            .filter(userCoupon -> userCoupon != null && userCoupon.getIsUsed())
            .forEach(userCoupon -> {
              userCoupon.updateIsUsed(false); // isUsed=false, isApplied=false로 롤백
            });

    order.setStatus(Order.OrderStatus.CANCELLED);
  }

  // 쿠폰 유효성 검사
  private void validateCouponForOrder(
          UserCoupon userCoupon,
          Long consumerId,
          Product product,
          BigDecimal itemTotalPrice
  ) {
    // 소유자 확인
    if (!userCoupon.getConsumer().getId().equals(consumerId)) {
      throw new SecurityException("본인 소유의 쿠폰이 아닙니다.");
    }

    // 이미 사용 완료한 쿠폰
    if (userCoupon.getIsUsed()) {
      throw new IllegalStateException("이미 사용 완료된 쿠폰입니다.");
    }

    // 쿠폰 활성화 여부
    if (!userCoupon.getCoupon().getIsActive()) {
      throw new IllegalArgumentException("사용 불가능한 쿠폰입니다.");
    }

    // 최소 주문 금액 확인
    BigDecimal minOrderAmount = new BigDecimal(userCoupon.getCoupon().getMinOrderAmount());
    if (itemTotalPrice.compareTo(minOrderAmount) < 0) {
      throw new IllegalArgumentException(
              String.format("상품 '%s'의 금액(%s원)이 쿠폰 최소 주문 금액(%s원)보다 적습니다.",
                      product.getName(), itemTotalPrice, minOrderAmount)
      );
    }
  }
  // 할인 금액 계산 처리- 소수점 이하는 버림
  private BigDecimal calculateDiscountAmount(BigDecimal productPrice, Coupon coupon) {
    try {
      BigDecimal discountValue = new BigDecimal(coupon.getDiscountValue());
      BigDecimal discountAmount = BigDecimal.ZERO;

      // 100 초과면 가격 할인 (Fixed Amount), 100 이하면 퍼센트 할인
      if (discountValue.compareTo(new BigDecimal("100")) > 0) {
        discountAmount = discountValue;
      } else {
        // 퍼센트 할인 (Percentage)
        BigDecimal percentage = discountValue.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        // 소수점 이하는 버림
        discountAmount = productPrice.multiply(percentage).setScale(0, RoundingMode.DOWN);
      }

      if (discountAmount.compareTo(productPrice) > 0) {
        discountAmount = productPrice;
      }

      return discountAmount; // BigDecimal 반환
    } catch (NumberFormatException e) {
      System.err.println("Coupon ID " + coupon.getId() + " has invalid discountValue.");
      return BigDecimal.ZERO;
    }
  }
}


