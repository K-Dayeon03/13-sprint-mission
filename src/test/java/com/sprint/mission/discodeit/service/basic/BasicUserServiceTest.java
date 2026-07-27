package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.command.CreateUserCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserCommand;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock UserRepository userRepository;
    @Mock BinaryContentRepository binaryContentRepository;
    @Mock BinaryContentStorage binaryContentStorage;
    @Mock UserStatusRepository userStatusRepository;
    @Mock MessageRepository messageRepository;
    @Mock ChannelRepository channelRepository;
    @Mock ReadStatusRepository readStatusRepository;
    @Mock UserMapper userMapper;

    @InjectMocks BasicUserService userService;

    private User user;
    private UserStatus userStatus;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User("woody", "woody1234", "woody@codeit.com", null);
        setId(user, UUID.randomUUID());
        userStatus = new UserStatus(user, Instant.now());
        setId(userStatus, UUID.randomUUID());
        userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, true);
    }

    @Test
    @DisplayName("유저 생성 성공")
    void create_success() {
        CreateUserCommand command = new CreateUserCommand("woody", "woody@codeit.com", "woody1234");

        given(userRepository.existsByUsernameOrEmail("woody", "woody@codeit.com")).willReturn(false);
        given(userRepository.saveAndFlush(any(User.class))).willAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            setId(saved, user.getId());
            return saved;
        });
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
        given(userMapper.toDto(any(User.class), any(UserStatus.class))).willReturn(userDto);

        UserDto result = userService.create(command, null);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).saveAndFlush(any(User.class));
        verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("유저 생성 실패 - username 또는 email 중복")
    void create_fail_duplicateUsernameOrEmail() {
        CreateUserCommand command = new CreateUserCommand("woody", "woody@codeit.com", "woody1234");
        given(userRepository.existsByUsernameOrEmail("woody", "woody@codeit.com")).willReturn(true);

        assertThatThrownBy(() -> userService.create(command, null))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("이미 사용 중");
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("프로필 이미지와 함께 유저 생성 시 파일 데이터는 Storage에 저장한다")
    void create_withProfileImage_storesBytes() {
        byte[] bytes = new byte[]{1, 2, 3};
        CreateUserCommand command = new CreateUserCommand("woody", "woody@codeit.com", "woody1234");
        BinaryContentCommand profileCommand = new BinaryContentCommand("profile.png", "image/png", bytes);
        UUID profileId = UUID.randomUUID();

        given(userRepository.existsByUsernameOrEmail("woody", "woody@codeit.com")).willReturn(false);
        given(userRepository.saveAndFlush(any(User.class))).willAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            setId(saved, user.getId());
            setId(saved.getProfile(), profileId);
            return saved;
        });
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
        given(userMapper.toDto(any(User.class), any(UserStatus.class))).willReturn(userDto);

        UserDto result = userService.create(command, profileCommand);

        assertThat(result).isEqualTo(userDto);
        verify(binaryContentStorage).put(profileId, bytes);
    }

    @Test
    @DisplayName("유저 단건 조회 성공")
    void findById_success() {
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userStatusRepository.findByUser_Id(user.getId())).willReturn(Optional.of(userStatus));
        given(userMapper.toDto(user, userStatus)).willReturn(userDto);

        UserDto result = userService.findById(user.getId());

        assertThat(result).isEqualTo(userDto);
    }

    @Test
    @DisplayName("유저 단건 조회 실패 - 존재하지 않는 유저")
    void findById_fail_notFound() {
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("존재하지 않는 사용자");
    }

    @Test
    @DisplayName("유저 전체 조회 성공")
    void findByAll_success() {
        given(userRepository.findAll()).willReturn(List.of(user));
        given(userStatusRepository.findByUser_Id(user.getId())).willReturn(Optional.of(userStatus));
        given(userMapper.toDto(user, userStatus)).willReturn(userDto);

        List<UserDto> result = userService.findByAll();

        assertThat(result).containsExactly(userDto);
    }

    @Test
    @DisplayName("유저 수정은 변경 감지로 처리한다")
    void update_success() {
        UpdateUserCommand command = new UpdateUserCommand("newWoody", null, null);
        UserDto updatedDto = new UserDto(user.getId(), "newWoody", user.getEmail(), null, true);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.existsByUsernameAndIdNot("newWoody", user.getId())).willReturn(false);
        given(userStatusRepository.findByUser_Id(user.getId())).willReturn(Optional.of(userStatus));
        given(userMapper.toDto(user, userStatus)).willReturn(updatedDto);

        UserDto result = userService.update(user.getId(), command, null);

        assertThat(result).isEqualTo(updatedDto);
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("유저 수정 실패 - username 중복")
    void update_fail_duplicateUsername() {
        UpdateUserCommand command = new UpdateUserCommand("buzz", null, null);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.existsByUsernameAndIdNot("buzz", user.getId())).willReturn(true);

        assertThatThrownBy(() -> userService.update(user.getId(), command, null))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("이미 사용 중");
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteById_success() {
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(channelRepository.findAll()).willReturn(List.of());
        given(messageRepository.findAll()).willReturn(List.of());

        userService.deleteById(user.getId());

        verify(readStatusRepository).deleteByUser_Id(user.getId());
        verify(userStatusRepository).deleteByUser_Id(user.getId());
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    @DisplayName("유저 삭제 시 작성한 채널 데이터도 함께 삭제한다")
    void deleteById_deletesAuthoredChannelData() {
        Channel authoredChannel = new Channel(ChannelType.PUBLIC, "general", "general channel", user.getId());
        setId(authoredChannel, UUID.randomUUID());

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(channelRepository.findAll()).willReturn(List.of(authoredChannel));
        given(messageRepository.findByChannel_Id(authoredChannel.getId())).willReturn(List.of());
        given(messageRepository.findAll()).willReturn(List.of());

        userService.deleteById(user.getId());

        verify(messageRepository, times(2)).deleteAll(List.of());
        verify(readStatusRepository).deleteByChannel_Id(authoredChannel.getId());
        verify(readStatusRepository).deleteByUser_Id(user.getId());
        verify(userRepository).deleteById(user.getId());
    }

    private static void setId(Object entity, UUID id) {
        ReflectionTestUtils.setField(entity, "id", id);
    }
}
