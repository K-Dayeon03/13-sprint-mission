package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.LoginCommand;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserStatusRepository userStatusRepository;
    @InjectMocks BasicAuthService authService;

    @Test
    @DisplayName("로그인 성공 - UserStatus 마지막 접속 시간 갱신")
    void login_success_updatesLastActiveAt() {
        // given
        User user = new User("woody", "woody1234", "woody@codeit.com", null);
        Instant oldLastActiveAt = Instant.now().minusSeconds(600);
        UserStatus userStatus = new UserStatus(user.getId(), oldLastActiveAt);
        LoginCommand request = new LoginCommand("woody", "woody1234");

        given(userRepository.findByUsername("woody")).willReturn(Optional.of(user));
        given(userStatusRepository.findByUserId(user.getId())).willReturn(Optional.of(userStatus));

        // when
        UserDto response = authService.login(request);

        // then
        assertThat(response.username()).isEqualTo("woody");
        assertThat(userStatus.getLastActiveAt()).isAfter(oldLastActiveAt);
        verify(userStatusRepository).save(userStatus);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() {
        // given
        User user = new User("woody", "woody1234", "woody@codeit.com", null);
        LoginCommand request = new LoginCommand("woody", "wrong-password");

        given(userRepository.findByUsername("woody")).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유저 이름 또는 비밀번호");
        verify(userStatusRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
