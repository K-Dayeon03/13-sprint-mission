package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
            .authorizeHttpRequests(auth -> auth
                    // 인증 흐름 진입점은 로그인 전에도 접근할 수 있어야 한다.
                    .requestMatchers("/api/auth/csrf-token").permitAll()
                    .requestMatchers("/api/auth/login").permitAll()
                    .requestMatchers("/api/auth/logout").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                    // 정적 화면, Swagger, Actuator 등 API 보호 대상이 아닌 요청은 허용한다.
                    .requestMatchers(
                            "/",
                            "/index.html",
                            "/assets/**",
                            "/favicon.ico",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/actuator/**",
                            "/error"
                    ).permitAll()

                    // 위에서 열어둔 요청 외에는 모두 인증이 필요하다.
                    .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                    // 로그인하지 않은 사용자가 보호된 API를 호출하면 401을 반환한다.
                    .authenticationEntryPoint((request, response, authException) ->
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                    )
                    // 로그인은 했지만 권한이 부족하면 403을 반환한다.
                    .accessDeniedHandler((request, response, accessDeniedException) ->
                            response.sendError(HttpServletResponse.SC_FORBIDDEN)
                    )
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

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_ADMIN > ROLE_CHANNEL_MANAGER
                ROLE_CHANNEL_MANAGER > ROLE_USER
                """);
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy
    ) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }
}
