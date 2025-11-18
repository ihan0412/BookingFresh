package est.oremi.backend12.bookingfresh.domain.order.dto;

import est.oremi.backend12.bookingfresh.domain.order.Order;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryUpdateRequest {
  private String address;
  private String detailAddress;
  private String email;
  private String request;
  private LocalDate deliveryDate;
  private Order.DeliverySlot deliverySlot;
}
