package est.oremi.backend12.bookingfresh.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

/*
    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring() // 정적 리소스 접근 허용
                .requestMatchers("/static/**", "/css/**", "/js/**");
    }

*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity // -> http 랑 다른게 뭘까
                .csrf(auth -> auth.disable())  // csrf 비활성화 -> JWT 환경에선 불필요?
                .httpBasic(AbstractHttpConfigurer::disable) // http basic 인증 비활성화 -> ?
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
                                        //"/api/auth/refresh",        // 토큰 재발급 처리
                                        "/api/login"     // POST /api/login (로그인 처리)
                                ).permitAll()
                                .requestMatchers("/static/**", "/css/**", "/js/**").permitAll() // 정적 리소스 접근 가능하게
                                .anyRequest().authenticated());

        // todo: JWT 로직 구현

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
