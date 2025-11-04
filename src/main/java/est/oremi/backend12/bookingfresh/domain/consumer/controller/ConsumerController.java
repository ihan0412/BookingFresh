package est.oremi.backend12.bookingfresh.domain.consumer.controller;

import est.oremi.backend12.bookingfresh.domain.consumer.Service.ConsumerService;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.AddConsumerRequest;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.ConsumerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConsumerController {
    private final ConsumerService consumerService;

    @PostMapping("/signup")
    public ResponseEntity<ConsumerResponse> signup(@Valid @RequestBody AddConsumerRequest request,
                                                   BindingResult bindingResult) {
        // DTO 유효성 검사 (정규식, @NotBlank 등) 실패 시 처리
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError().getDefaultMessage();
            // 에러 메시지와 함께 400 Bad Request 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ConsumerResponse(errorMsg));
        }

        try {
            // Service 로직 호출 (비밀번호 일치, 중복 확인 포함)
            ConsumerResponse response = consumerService.signUp(request);
            // 성공 시 201 Created 반환
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // 비즈니스 로직 예외 처리 (비밀번호 불일치, 중복 이메일/닉네임 등)
            // 에러 메시지와 함께 400 Bad Request 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ConsumerResponse(e.getMessage()));

        } catch (Exception e) {
            // 기타 서버 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ConsumerResponse("서버 오류가 발생했습니다."));
        }

    }

    // todo: 로그인
}
