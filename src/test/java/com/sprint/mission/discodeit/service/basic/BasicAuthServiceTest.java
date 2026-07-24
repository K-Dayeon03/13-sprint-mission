package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.LoginCommand;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserStatusRepository userStatusRepository;
    @Mock UserMapper userMapper;

    @InjectMocks BasicAuthService authService;

    @Test
    @DisplayName("로그인 성공 - UserStatus 마지막 접속 시간 갱신")
    void login_success_updatesLastActiveAt() {
        User user = new User("woody", "woody1234", "woody@codeit.com", null);
        setId(user, UUID.randomUUID());
        Instant oldLastActiveAt = Instant.now().minusSeconds(600);
        UserStatus userStatus = new UserStatus(user, oldLastActiveAt);
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, true);
        LoginCommand command = new LoginCommand("woody", "woody1234");

        given(userRepository.findByUsername("woody")).willReturn(Optional.of(user));
        given(userStatusRepository.findByUser_Id(user.getId())).willReturn(Optional.of(userStatus));
        given(userMapper.toDto(user, userStatus)).willReturn(userDto);

        UserDto result = authService.login(command);

        assertThat(result).isEqualTo(userDto);
        assertThat(userStatus.getLastActiveAt()).isAfter(oldLastActiveAt);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() {
        User user = new User("woody", "woody1234", "woody@codeit.com", null);
        LoginCommand command = new LoginCommand("woody", "wrong-password");

        given(userRepository.findByUsername("woody")).willReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(command))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("유저 이름 또는 비밀번호");
        verify(userStatusRepository, never()).findByUser_Id(org.mockito.ArgumentMatchers.any());
    }

    private static void setId(Object entity, UUID id) {
        ReflectionTestUtils.setField(entity, "id", id);
    }
}
