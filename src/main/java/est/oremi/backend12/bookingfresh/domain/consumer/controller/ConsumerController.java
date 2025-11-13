package est.oremi.backend12.bookingfresh.domain.consumer.controller;

import est.oremi.backend12.bookingfresh.config.jwt.JwtTokenProvider;
import est.oremi.backend12.bookingfresh.domain.consumer.Service.ConsumerService;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.*;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.token.TokenService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConsumerController {
    private final ConsumerService consumerService;
    private final JwtTokenProvider jwtTokenProvider;
/*
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
*/

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody AddConsumerRequest request,
                                    BindingResult bindingResult) {

        // DTO 유효성 검사 실패 시 필드별 에러 리스트 반환
        if (bindingResult.hasErrors()) {
            List<FieldErrorResponse> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldErrorResponse(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errors", errors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            ConsumerResponse response = consumerService.signUp(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // 비즈니스 로직 오류는 message로 반환
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult,
            HttpServletResponse response) { // Refresh Token을 HttpOnly Cookie로 설정하기위한 HttpServletResponse

        // 유효성 검사 실패 시 필드별 에러 반환
        if (bindingResult.hasErrors()) {
            List<FieldErrorResponse> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldErrorResponse(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errors", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            TokenResponse tokenResponse = consumerService.login(request);

            // Refresh Token을 HttpOnly 쿠키로 변환하여 응답 헤더에 추가
            ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(true) // HTTP 환경 테스트를 위해 false (운영 시 true)
                    .path("/api") // api 경로에 쿠키 전송
                    .sameSite("Strict")
                    .maxAge(jwtTokenProvider.getRefreshTokenExpirationSeconds()) // RT 만료 시간
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            // 응답 Body에는 Access Token만 포함하여 반환
            return ResponseEntity.ok(
                    TokenResponse.builder()
                            .accessToken(tokenResponse.getAccessToken())
                            // Refresh Token은 Body에서 제거하거나 null 처리
                            // Body에 RT가 남아 있으면 프론트가 오용할 수 있으므로 제거하는 것이 좋음
                            .build()
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //토큰 재발급 API: Refresh Token을 이용해 Access Token과 Refresh Token 재발급
    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshRequest request,
                                                      BindingResult bindingResult) {

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            // Service 로직 호출, 성공시 새 토큰 반환
            TokenResponse tokenResponse = consumerService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(tokenResponse);
        } catch (IllegalArgumentException e) {
            // Refresh Token 무효/만료 등 실패 시 (401 Unauthorized)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 로그아웃 API: Refresh Token DB 삭제 및 쿠키 무효화
    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {

        // Todo: DB Refresh Token 삭제 로직 (ConsumerService에 구현 필요), 리팩토링?
        // 간단하게 쿠키 자체를 조회해 삭제
        consumerService.logout(refreshToken);

        // 브라우저의 Refresh Token 쿠키를 만료시켜 삭제
        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/") // 모든 경로에서 쿠키 삭제
                .maxAge(0) // 만료 시간을 0으로 설정하여 즉시 삭제
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .body("로그아웃 성공");
    }

    @PatchMapping("/me")
    public ResponseEntity<ConsumerResponse> updateConsumerInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ConsumerUpdateRequest request) {

        //현재 로그인된 사용자의 ID 가져옴
        // Long consumerId = customUserDetails.getId();
        Long consumerId = 1L; // 일단 하드코딩으로 진행
        // 서비스 메서드 호출
        ConsumerResponse updatedConsumer = consumerService.updateConsumerInfo(
                consumerId,
                request
        );

        return ResponseEntity.ok(updatedConsumer);
    }
}
