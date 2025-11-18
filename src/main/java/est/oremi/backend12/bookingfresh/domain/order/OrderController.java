package est.oremi.backend12.bookingfresh.domain.order;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import est.oremi.backend12.bookingfresh.domain.order.Order.DeliverySlot;
import est.oremi.backend12.bookingfresh.domain.order.dto.DeliveryUpdateRequest;
import est.oremi.backend12.bookingfresh.domain.order.dto.OrderDto;
import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  // 주문 생성 (JWT에서 consumerId 추출)
  @PostMapping("/create")
  public ResponseEntity<OrderDto> createOrder(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam boolean isReservation,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deliveryDate,
      @RequestParam(required = false) Order.DeliverySlot deliverySlot) {

    Long consumerId = userDetails.getId(); //JWT에서 추출

    Long orderId = orderService.createOrder(consumerId, deliveryDate, deliverySlot, isReservation);
    OrderDto orderDto = orderService.getOrder(orderId);
    return ResponseEntity.ok(orderDto);
  }

  // 주문 조회
  @GetMapping("/{orderId}")
  public ResponseEntity<OrderDto> getOrder(@PathVariable Long orderId) {
    OrderDto orderDto = orderService.getOrder(orderId);
    return ResponseEntity.ok(orderDto);
  }

  // 주문 수정
  @PatchMapping("/{orderId}/update-delivery")
  public ResponseEntity<Void> updateDeliveryInfo(
      @PathVariable Long orderId,
      @RequestBody DeliveryUpdateRequest request) {
    orderService.updateDeliveryInfo(orderId, request);
    return ResponseEntity.ok().build();
  }

  // 주문 취소
  @PatchMapping("/{orderId}/cancel")
  public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
    orderService.cancelOrder(orderId);
    return ResponseEntity.ok().build();
  }
  @PatchMapping("/{orderId}/complete")
  public ResponseEntity<Void> completeOrder(
      @PathVariable Long orderId,
      @RequestBody DeliveryUpdateRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long consumerId = userDetails.getId();
    orderService.completeOrder(orderId, consumerId, request);
    return ResponseEntity.ok().build();
  }

  // 내 모든 주문 조회 (JWT 기반)
  @GetMapping("/my")
  public ResponseEntity<List<OrderDto>> getConsumerOrders(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long consumerId = userDetails.getId();
    List<OrderDto> orders = orderService.getOrdersByConsumerId(consumerId);
    return ResponseEntity.ok(orders);
  }
}


