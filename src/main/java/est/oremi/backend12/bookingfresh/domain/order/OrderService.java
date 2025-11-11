package est.oremi.backend12.bookingfresh.domain.order;

import est.oremi.backend12.bookingfresh.domain.cart.Cart;
import est.oremi.backend12.bookingfresh.domain.cart.CartItem;
import est.oremi.backend12.bookingfresh.domain.cart.CartItemRepository;
import est.oremi.backend12.bookingfresh.domain.cart.CartRepository;
import est.oremi.backend12.bookingfresh.domain.order.dto.OrderDto;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    // 총액 계산 부분
//    for (CartItem cartItem : cart.getItems()) {
//      OrderItem orderItem = new OrderItem();
//      orderItem.setOrder(order);
//      orderItem.setProduct(cartItem.getProduct());
//      orderItem.setQuantity(cartItem.getQuantity());
//
//      order.getOrderItems().add(orderItem);
//
//      BigDecimal itemTotal = cartItem.getProduct().getPrice()
//          .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
//      totalPrice = totalPrice.add(itemTotal);
//    }

    order.setTotalPrice(totalPrice);
    order.setFinal_cost(totalPrice); // 쿠폰 적용 전이므로 동일

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
    order.setStatus(Order.OrderStatus.CANCELLED);
  }
}


