package est.oremi.backend12.bookingfresh.domain.consumer.dto;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumerResponse {
    private String email;
    private String password;
    private String nickname;
    private String address;
    private String detailAddress;

    private String message;

    public ConsumerResponse(Consumer consumer) {
        this.email = consumer.getEmail();
        this.password = consumer.getPassword();
        this.nickname = consumer.getNickname();
        this.address = consumer.getAddress();
        this.detailAddress = consumer.getDetailAddress();
        this.message = "회원가입 성공";
    }

    public ConsumerResponse(String message) {
        this.message = message;
    }
}
