package est.oremi.backend12.bookingfresh.domain.consumer.dto;

import lombok.Getter;

@Getter
public class ConsumerUpdateRequest {
    // 닉네임 변경
    private String nickname;

    // 주소 변경
    private String address;
    private String detailAddress;

    // 비밀번호 변경
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm; // 새 비밀번호 확인
}
// 모든 컬럼의 옵셔널 로직은 서비스단에서 null 로 감지