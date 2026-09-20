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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //클라이언트에서 쿠키에 저장된 CSRF 토큰에 접근해야 하므로 Http Only는 false로 설정
       //csrf 설정, 브라우저에서 XSRF-TOKEN 쿠키를 읽을 수 있도록 HttpOnly=false로 둔다.
        http.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        //Spring Security의 formLogin 기능을 킨다.
        //이 설정을 하면 UsernamePasswordAuthenticationFilter가 로그인 요청을 처리한다.
                //기본값은 /login
                //요구사항 로그인 처리 url /api/auth/login로 설정
                //이 주소로 post 요청이 오면 컨트롤러가 아니라 Spring Security필터가 먼저 로그인 처리한다는 의미
        ).formLogin(login -> login.loginProcessingUrl("/api/auth/login")
                //로그인 성공 시 200 UserReponse를 직접 응답
                .successHandler(loginSuccessHandler)

                //로그인 실패 시 401 ErrorResponse를직접 응답
                .failureHandler(loginFailureHandler)


        );
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
