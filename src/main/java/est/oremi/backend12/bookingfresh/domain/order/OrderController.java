package est.oremi.backend12.bookingfresh.domain.order;

import est.oremi.backend12.bookingfresh.domain.order.dto.OrderDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  // 주문 생성
  @PostMapping("/create")
  public ResponseEntity<Long> createOrder(
      @RequestParam Long consumerId,
      @RequestParam LocalDateTime deliveryDateTime,
      @RequestParam boolean isReservation) {
    Long orderId = orderService.createOrder(consumerId, deliveryDateTime, isReservation);
    return ResponseEntity.ok(orderId);
  }

  // 주문 조회
  @GetMapping("/{orderId}")
  public ResponseEntity<OrderDto> getOrder(@PathVariable Long orderId) {
    OrderDto orderDto = orderService.getOrder(orderId);
    return ResponseEntity.ok(orderDto);
  }

  // 주문 취소
  @PatchMapping("/{orderId}/cancel")
  public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
    orderService.cancelOrder(orderId);
    return ResponseEntity.ok().build();
  }
}


