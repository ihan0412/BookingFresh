package est.oremi.backend12.bookingfresh.config;

import est.oremi.backend12.bookingfresh.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity // -> http 랑 다른게 뭘까
                .csrf(auth -> auth.disable())
                // csrf 비활성화 -> JWT 환경에선 불필요? -> 세션을 사용하지 않기 때문에
                .httpBasic(AbstractHttpConfigurer::disable)
                // http basic 인증 비활성화 -> 사용자면  비밀번호를 텍스트로 전송하는 기본적인 인증 방식,
                // 보안에 취약하므로 JWT 롸 같이 암호화된 토큰 기반의 인증 방식을 사용할 때는 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) // -> JWT 를 사용하기 때문에 세션을 생성하거나 사용하지 않도록 설정(stateless)
                .authorizeHttpRequests(auth ->   // 인증, 인가 설정
                        auth
                                .requestMatchers(
                                        "/signup",
                                        "/login",
                                        "/home",
                                        "/api/signup",   // POST /api/signup (회원가입 처리)
                                        "/api/auth/refresh",        // 토큰 재발급 처리
                                        "/api/auth/logout",
                                        "/api/coupons",
                                        "/cart/add",
                                        "/api/coupons/cart/item/coupon",
                                        "/orders/create",
                                        "/api/me", // 개인정보 수정
                                        "/api/coupons/consumer/*", // 사용자 쿠폰 조회
                                        "/api/coupons/available/*/consumer/*/prices", // 상품 적용 가능 쿠폰 조회 + 적용 가격 포함
                                        // "/api/coupons/available/*/consumer/*", // 상품 적용 가능 쿠폰 조회, /api/coupons/available/{productId}/consumer/{consumerId}
                                        "/api/login"     // POST /api/login (로그인 처리)
                                ).permitAll()
                                .requestMatchers("/static/**", "/css/**", "/js/**").permitAll() // 정적 리소스 접근 가능하게
                                .anyRequest().authenticated()

                )
                // 토큰 필터 먼저 적용
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);;

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // 비밀번호 검증
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
