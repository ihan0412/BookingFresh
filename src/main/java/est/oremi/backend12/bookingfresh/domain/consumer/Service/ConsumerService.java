package est.oremi.backend12.bookingfresh.domain.consumer.Service;

import est.oremi.backend12.bookingfresh.config.jwt.JwtTokenProvider;
import est.oremi.backend12.bookingfresh.domain.consumer.ConsumerRepository;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.AddConsumerRequest;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.ConsumerResponse;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.LoginRequest;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.TokenResponse;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.RefreshToken;
import est.oremi.backend12.bookingfresh.domain.consumer.repository.RefreshTokenRepository;
import est.oremi.backend12.bookingfresh.domain.coupon.service.CouponService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsumerService {
    private final ConsumerRepository consumerRepository;
    private final PasswordEncoder encoder;
    private final CouponService couponService;
    @Transactional
    public ConsumerResponse signUp(AddConsumerRequest request) {
        // 비밀번호, 비밀번호 확인 일치 검사
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        // 이메일 중복 확인
        if (consumerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
        }
        // 닉네임 중복 확인
        if (consumerRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임 입니다.");
        }

        String encodedPassword = encoder.encode(request.getPassword());
        // Entity 생성 및 저장
        Consumer newConsumer = Consumer.builder()
                .email(request.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .nickname(request.getNickname())
                .address(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .createdAt(LocalDateTime.now())
                .build();

        Consumer savedConsumer = consumerRepository.save(newConsumer);
        // 회원가입한 사용자에게 현재 존재하는 모든 쿠폰 발행
        couponService.issueAllActiveCouponsToNewConsumer(savedConsumer);
        return new ConsumerResponse(newConsumer);
    }


    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenResponse login(LoginRequest request) {

        // Spring Security를 이용한 비밀번호 검증 (AuthenticationManager 사용)
        // singleton 빈으로 등록 되어있어 나중에 사용자 정보가 필요할 떄 Principal 필드 사용 할수 있도록
        Authentication authentication;
        try {
            // CustomUserDetailsService를 통해 이메일로 사용자 로드, PasswordEncoder로 비밀번호 검증
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            // 비밀번호 불일치, 사용자 없음 등 인증 실패 시
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 인증된 사용자 정보 (Consumer) 로드
        Consumer consumer = consumerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("인증되었으나 사용자 정보를 찾을 수 없습니다."));

        // Access Token / Refresh Token 생성
        String role = "USER"; // CustomUserDetails에서 권한을 List.of()로 설정했으므로, 임시 Role 사용
        String accessToken = jwtTokenProvider.generateAccessToken(consumer.getEmail(), role);
        String refreshToken = jwtTokenProvider.generateRefreshToken(consumer.getEmail(), role);

        // Refresh Token의 만료 시점 계산
        long refreshTokenExpirationMs = jwtTokenProvider.getRefreshTokenExpirationSeconds() * 1000;
        LocalDateTime expiryDate = new Date(System.currentTimeMillis() + refreshTokenExpirationMs)
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();


        // Refresh Token RDB 저장 또는 갱신
        // 이미 해당 userId의 Refresh Token이 존재하는지 확인
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(consumer.getId());

        if (existingToken.isPresent()) {
            // 존재하면 토큰 값 및 만료 시간 갱신
            existingToken.get().updateToken(refreshToken, expiryDate);
        } else {
            // 존재하지 않으면 새로 생성하여 저장
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .token(refreshToken)
                    .userId(consumer.getId())
                    .expiryDate(expiryDate)
                    .build();
            refreshTokenRepository.save(newRefreshToken);
        }

        // Access/Refresh Token 반환
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Refresh Token을 이용해 Access Token과 새로운 Refresh Token을 재발급합니다.
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {

        // Refresh Token 유효성 검증 (JWT 서명, 기본 만료 시간 등)
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            // ExpiredJwtException 등 예외는 validateToken에서 이미 로그 처리됨.
            throw new IllegalArgumentException("유효하지 않거나 변조된 Refresh Token입니다.");
        }

        // 토큰에서 사용자 정보 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        String role = jwtTokenProvider.getRoleFromToken(refreshToken);

        // Refresh Token 문자열로 DB에서 엔티티 조회 (탈취/로그아웃 여부 확인)
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("DB에 존재하지 않는 Refresh Token입니다. (이미 로그아웃되었거나 유효하지 않음)"));

        // DB에 저장된 만료 시간 확인 (RDB 관리 기준)
        if (storedToken.isExpired()) {
            // RDB 만료 시간이 지났다면, DB에서 삭제하고 예외 발생
            refreshTokenRepository.delete(storedToken);
            throw new IllegalArgumentException("만료된 Refresh Token입니다. 다시 로그인해야 합니다.");
        }

        // Access/Refresh Token 재발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(email, role);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email, role);

        // Refresh Token의 만료 시점 재계산
        long newExpirationMs = jwtTokenProvider.getRefreshTokenExpirationSeconds() * 1000;
        LocalDateTime newExpiryDate = new Date(System.currentTimeMillis() + newExpirationMs)
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // DB의 Refresh Token 정보 갱신 (Dirty Checking)
        storedToken.updateToken(newRefreshToken, newExpiryDate);

        // 새로운 토큰 반환
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            System.err.println("로그아웃 실패: RefreshToken이 null/empty 입니다.");
            return;
        }

        try {
            //  refreshToken 에서 Email 추출 (만료 예외 발생 가능성 있음)
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);

            // Email로 Consumer 엔티티를 조회하여 userId 획득
            Consumer consumer = consumerRepository.findByEmail(email)
                    .orElse(null); // 사용자를 찾지 못하면 null

            if (consumer != null) {
                // userId를 이용하여 Refresh Token 삭제
                refreshTokenRepository.deleteByUserId(consumer.getId());
                System.out.println("로그아웃: RT 엔티티 삭제 명령 완료 (UserID: " + consumer.getId() + ")");
            } else {
                System.err.println("로그아웃 실패: DB에서 해당 이메일(" + email + ")의 사용자를 찾을 수 없음.");
            }

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 토큰이 만료되었을 경우: 클레임에서 이메일 추출하여 강제 삭제 시도
            String expiredEmail = e.getClaims().getSubject();
            consumerRepository.findByEmail(expiredEmail).ifPresent(consumer -> {
                refreshTokenRepository.deleteByUserId(consumer.getId());
                System.out.println("로그아웃: 만료된 RT 삭제 완료 (UserID: " + consumer.getId() + ")");
            });

        } catch (Exception e) {
            System.err.println("로그아웃 최종 실패! 원인: " + e.getMessage());
            throw new RuntimeException("Refresh Token 삭제 중 오류 발생", e);
        }
    }
}
