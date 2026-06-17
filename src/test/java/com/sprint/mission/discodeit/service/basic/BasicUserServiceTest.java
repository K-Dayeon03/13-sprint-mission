package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock UserRepository userRepository;
    @Mock BinaryContentRepository binaryContentRepository;
    @Mock UserStatusRepository userStatusRepository;
    @Mock MessageRepository messageRepository;
    @Mock ChannelRepository channelRepository;
    @Mock ReadStatusRepository readStatusRepository;
    @InjectMocks BasicUserService userService;

    private User user;
    private UserStatus userStatus;

    @BeforeEach
    void setUp() {
        user = new User("woody", "woody1234", "woody@codeit.com", null);
        userStatus = new UserStatus(user.getId(), Instant.now());
    }

    @Test
    @DisplayName("유저 생성 성공")
    void create_success() {
        // given
        CreateUserRequest request = new CreateUserRequest("woody", "woody@codeit.com", "woody1234");
        given(userRepository.findByAll()).willReturn(List.of());
        given(userRepository.save(any())).willReturn(user);
        given(userStatusRepository.save(any())).willReturn(userStatus);
        // given(userStatusRepository.findByUserId(any())).willReturn(Optional.of(userStatus)); ← 제거

        // when
        UserResponse response = userService.create(request, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo("woody");
        assertThat(response.email()).isEqualTo("woody@codeit.com");
        verify(userRepository).save(any());
        verify(userStatusRepository).save(any());
    }
    @Test
    @DisplayName("유저 생성 실패 - username 중복")
    void create_fail_duplicateUsername() {
        // given
        CreateUserRequest request = new CreateUserRequest("woody", "other@codeit.com", "pass1234");
        given(userRepository.findByAll()).willReturn(List.of(user));

        // when & then
        assertThatThrownBy(() -> userService.create(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용 중인");
    }

    @Test
    @DisplayName("유저 생성 실패 - email 중복")
    void create_fail_duplicateEmail() {
        // given
        CreateUserRequest request = new CreateUserRequest("other", "woody@codeit.com", "pass1234");
        given(userRepository.findByAll()).willReturn(List.of(user));

        // when & then
        assertThatThrownBy(() -> userService.create(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용 중인");
    }

    @Test
    @DisplayName("유저 단건 조회 성공")
    void findById_success() {
        // given
        given(userRepository.findById(user.getId())).willReturn(user);
        given(userStatusRepository.findByUserId(user.getId())).willReturn(Optional.of(userStatus));

        // when
        UserResponse response = userService.findById(user.getId());

        // then
        assertThat(response.id()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("유저 단건 조회 실패 - 존재하지 않는 유저")
    void findById_fail_notFound() {
        // given
        given(userRepository.findById(any())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> userService.findById(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 사용자");
    }

    @Test
    @DisplayName("유저 수정 성공")
    void update_success() {
        // given
        UpdateUserRequest request = new UpdateUserRequest("newWoody", null, null);
        given(userRepository.findById(user.getId())).willReturn(user);
        given(userRepository.save(any())).willReturn(user);
        given(userStatusRepository.findByUserId(user.getId())).willReturn(Optional.of(userStatus));

        // when
        UserResponse response = userService.update(user.getId(), request, null);

        // then
        assertThat(response).isNotNull();
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteById_success() {
        // given
        given(userRepository.findById(user.getId())).willReturn(user);
        given(channelRepository.findByAll()).willReturn(List.of());

        // when
        userService.deleteById(user.getId());

        // then
        verify(messageRepository).deleteByAuthorId(user.getId());
        verify(channelRepository).deleteByAuthorId(user.getId());
        verify(userStatusRepository).deleteByUserId(user.getId());
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    @DisplayName("유저 전체 조회")
    void findByAll_success() {
        // given
        User user2 = new User("john", "john1234", "john@codeit.com", null);
        UserStatus userStatus2 = new UserStatus(user2.getId(), Instant.now());

        given(userRepository.findByAll()).willReturn(List.of(user, user2));
        given(userStatusRepository.findByUserId(user.getId())).willReturn(Optional.of(userStatus));
        given(userStatusRepository.findByUserId(user2.getId())).willReturn(Optional.of(userStatus2));

        // when
        List<UserResponse> responses = userService.findByAll();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses).extracting("username")
                .containsExactlyInAnyOrder("woody", "john");
    }

    @Test
    @DisplayName("유저 수정 실패 - 존재하지 않는 유저")
    void update_fail_notFound() {
        // given
        UpdateUserRequest request = new UpdateUserRequest("newWoody", null, null);
        given(userRepository.findById(any())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> userService.update(UUID.randomUUID(), request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 사용자");
    }

    @Test
    @DisplayName("유저 삭제 실패 - 존재하지 않는 유저")
    void deleteById_fail_notFound() {
        // given
        given(userRepository.findById(any())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> userService.deleteById(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 사용자");
    }

    @Test
    @DisplayName("프로필 이미지와 함께 유저 생성")
    void create_success_withProfileImage() {
        // given
        CreateUserRequest userRequest = new CreateUserRequest("woody", "woody@codeit.com", "woody1234");
        CreateBinaryContentRequest imageRequest = new CreateBinaryContentRequest(
                "profile.png", "image/png", new byte[]{1, 2, 3});

        BinaryContent savedImage = new BinaryContent(user.getId(), null,
                "profile.png", "image/png", new byte[]{1, 2, 3});

        given(userRepository.findByAll()).willReturn(List.of());
        given(userRepository.save(any())).willReturn(user);
        given(binaryContentRepository.save(any())).willReturn(savedImage);
        given(userStatusRepository.save(any())).willReturn(userStatus);

        // when
        UserResponse response = userService.create(userRequest, imageRequest);

        // then
        assertThat(response).isNotNull();
        verify(binaryContentRepository).save(any()); // 이미지 저장 확인
        verify(userRepository, times(2)).save(any()); // 유저 저장 2번 (최초 + profileImageId 반영)
    }

    @Test
    @DisplayName("프로필 이미지 교체")
    void update_success_withProfileImage() {
        // given
        UUID oldImageId = UUID.randomUUID();
        User userWithImage = new User("woody", "woody1234", "woody@codeit.com", oldImageId);
        UpdateUserRequest userRequest = new UpdateUserRequest(null, null, null);
        CreateBinaryContentRequest imageRequest = new CreateBinaryContentRequest(
                "new.png", "image/png", new byte[]{4, 5, 6});

        BinaryContent newImage = new BinaryContent(userWithImage.getId(), null,
                "new.png", "image/png", new byte[]{4, 5, 6});

        given(userRepository.findById(userWithImage.getId())).willReturn(userWithImage);
        given(binaryContentRepository.save(any())).willReturn(newImage);
        given(userRepository.save(any())).willReturn(userWithImage);
        given(userStatusRepository.findByUserId(userWithImage.getId())).willReturn(Optional.of(userStatus));

        // when
        userService.update(userWithImage.getId(), userRequest, imageRequest);

        // then
        verify(binaryContentRepository).deleteById(oldImageId); // 기존 이미지 삭제 확인
        verify(binaryContentRepository).save(any());            // 새 이미지 저장 확인
    }

}
