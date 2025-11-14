package est.oremi.backend12.bookingfresh.domain.order.dto;

import est.oremi.backend12.bookingfresh.domain.order.Order;
import est.oremi.backend12.bookingfresh.domain.order.Order.DeliverySlot;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDto {

  private Long orderId;                     // 주문 ID
  private BigDecimal totalPrice;           // 주문 총 금액 (할인 전)
  private BigDecimal finalCost;            // 최종 결제 금액 (할인/쿠폰 적용 후)
  private String status;                   // 주문 상태 (PENDING, COMPLETED, CANCELLED)
  private Boolean isReservation;           // 예약 주문 여부
  private LocalDate deliveryDate;           // 배송 날짜
  private DeliverySlot deliverySlot;        // 배송 시간대
  private LocalDateTime createdAt;         // 주문 생성 시간
  private Long consumerId;                 // 주문자 ID
  private String consumerName;             // 주문자 이름
  private List<OrderItemDto> items;        // 주문 상품 목록

  public static OrderDto from(Order order) {
    List<OrderItemDto> itemDtos = order.getOrderItems().stream()
        .map(item -> new OrderItemDto(
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getProduct().getPrice()
        ))
        .toList();

    return new OrderDto(
        order.getId(),
        order.getTotalPrice(),
        order.getFinal_cost(),
        order.getStatus().name(),
        order.getIsReservation(),
        order.getDeliveryDate(),
        order.getDeliverySlot(),
        order.getCreatedAt(),
        order.getConsumer().getId(),
        order.getConsumer().getNickname(),
        itemDtos
    );
  }
}


