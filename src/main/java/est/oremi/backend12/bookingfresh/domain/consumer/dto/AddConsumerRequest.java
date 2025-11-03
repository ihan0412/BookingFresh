package est.oremi.backend12.bookingfresh.domain.consumer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddConsumerRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).*$",
            message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    private String passwordConfirm;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    private String nickname;

    @NotBlank(message = "주소는 필수 입력 항목입니다.")
    private String address;

    @NotBlank(message = "세부 주소는 필수 입력 항목입니다.")
    private String detailAddress;

}
