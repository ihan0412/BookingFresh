package est.oremi.backend12.bookingfresh.domain.consumer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {

    @NotBlank(message = "Refresh Token은 필수 항목입니다.")
    private String refreshToken;
}