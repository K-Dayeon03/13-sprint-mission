package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {
    // 인증 성공 후 응답으로 내려줄 사용자 정보입니다.
    private final UserResponse userResponse;

    // DB에 저장된 BCrypt 해시 비밀번호입니다.
    // 원문 비밀번호가 아니라 "$2a$10$..." 형태의 값
    private final String password;

    // Spring Security는 hasRole("ADMIN")을 ROLE_ADMIN 권한으로 비교한다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userResponse.role().name()));
    }
    // Spring Security가 비밀번호를 비교할 때 사용하는 값입니다.
    @Override
    public String getPassword() {
        return password;
    }
    // Spring Security가 사용자 이름으로 인식할 값입니다.
    @Override
    public String getUsername() {
        return userResponse.username();
    }

    // 계정 만료 여부입니다.
    // true면 "만료되지 않음"이라는 뜻입니다.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부입니다.
    // true면 "잠기지 않음"이라는 뜻입니다.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부입니다.
    // true면 "비밀번호가 만료되지 않음"이라는 뜻입니다.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부입니다.
    // true면 "사용 가능한 계정"이라는 뜻입니다.
    @Override
    public boolean isEnabled() {
        return true;
    }
}
