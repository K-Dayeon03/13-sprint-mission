package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 클라이언트에서 XSRF-TOKEN 쿠키를 읽을 수 있도록 HttpOnly=false로 둔다.
        http.csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            )
            // POST /api/auth/login 요청은 컨트롤러가 아니라 Spring Security 필터가 처리한다.
            .formLogin(login -> login
                    .loginProcessingUrl("/api/auth/login")
                    // 로그인 성공 시 200 UserResponse를 직접 응답한다.
                    .successHandler(loginSuccessHandler)
                    // 로그인 실패 시 401 ErrorResponse를 직접 응답한다.
                    .failureHandler(loginFailureHandler)
            )
            // Spring Security의 기본 logout 흐름은 유지하고, URL과 성공 응답 방식만 교체한다.
            .logout(logout -> logout
                    // CSRF가 켜져 있으므로 POST /api/auth/logout 요청으로 처리된다.
                    .logoutUrl("/api/auth/logout")
                    // 기본 SimpleUrlLogoutSuccessHandler 대신 204 No Content를 반환한다.
                    .logoutSuccessHandler(
                            new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)
                    )
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
