package est.oremi.backend12.bookingfresh.domain.consumer.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    // 필요한 경우, 만료 시간 등 추가 가능
}