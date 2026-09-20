package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    //@AuthenticationPrincipal을 쓰는 이유는 로그인 성공 후 spring security가 내부적으로 Authentication -> principal = DiscodeitUserDetails를 들고있다.
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(userDetails.getUserResponse());
    }

    @PutMapping("/role")
    public ResponseEntity<UserResponse> updateRole(
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        UserResponse updatedUser = userService.updateRole(request.userId(), request.newRole());
        return ResponseEntity.ok(updatedUser);
    }

    /*.loginProcessingUrl("/api/auth/login")했기때문에 아래처럼 흐름
    * POST /api/auth/login
    → UsernamePasswordAuthenticationFilter
    → AuthenticationManager
    → DaoAuthenticationProvider
    → DiscodeitUserDetailsService
    → UserRepository
    → PasswordEncoder 자동 비교
    * */
    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);
        return ResponseEntity.noContent().build();

    }

}
