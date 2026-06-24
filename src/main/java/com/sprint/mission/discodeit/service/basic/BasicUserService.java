package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;


    @Override
    public UserResponse create(CreateUserRequest userRequest, CreateBinaryContentRequest profileImageRequest) {
        validateUsernameAndEmail(userRequest.username(), userRequest.email());

        User user = new User(userRequest.username(), userRequest.password(),
                userRequest.email(), null);
        saveUserWithProfileImage(user, profileImageRequest);

        UserStatus userStatus = createUserStatus(user.getId());
        return UserResponse.from(user, userStatus);
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // findByUserId()는 Optional 반환 → orElseThrow() 필요
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 UserStatus입니다."));
        return UserResponse.from(user, userStatus);
    }
    @Override
    public List<UserResponse> findByAll() {
        return userRepository.findByAll().stream()
                .map(user -> {
                    // 메서드명 누락 → findByUserId() + orElseThrow()
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 UserStatus입니다."));
                    return UserResponse.from(user, userStatus);
                })
                .toList();
    }

    @Override
    public UserResponse update(UUID id, UpdateUserRequest userRequest,
                               CreateBinaryContentRequest profileImageRequest) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 프로필 이미지 교체 시 기존 이미지 삭제 후 새로 저장
        UUID newProfileImageId = user.getProfileImageId();
        if (profileImageRequest != null) {
            if (user.getProfileImageId() != null) {
                binaryContentRepository.deleteById(user.getProfileImageId());
            }
            BinaryContent newImage = new BinaryContent(
                    user.getId(), // userId 설정
                    null,         // messageId는 null
                    profileImageRequest.fileName(),
                    profileImageRequest.contentType(),
                    profileImageRequest.bytes()
            );
            newProfileImageId = binaryContentRepository.save(newImage).getId();
        }

        user.update(userRequest.newUsername(), userRequest.newPassword(),
                userRequest.newEmail(), newProfileImageId);
        userRepository.save(user);

        UserStatus userStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 UserStatus입니다."));
        return UserResponse.from(user, userStatus);
    }

    @Override
    public void deleteById(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        deleteProfileImage(user);
        deleteAuthoredChannelData(id);
        deleteUserData(id);
    }

    private void validateUsernameAndEmail(String username, String email) {
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw new IllegalArgumentException("이미 사용 중인 유저 이름 또는 이메일 입니다.");
        }
    }

    private void saveUserWithProfileImage(User user, CreateBinaryContentRequest profileImageRequest) {
        userRepository.save(user);
        if (profileImageRequest == null) {
            return;
        }

        UUID profileImageId = saveProfileImage(user.getId(), profileImageRequest);
        user.update(null, null, null, profileImageId);
        userRepository.save(user);
    }

    private UUID saveProfileImage(UUID userId, CreateBinaryContentRequest profileImageRequest) {
        BinaryContent profileImage = new BinaryContent(
                userId,
                null,
                profileImageRequest.fileName(),
                profileImageRequest.contentType(),
                profileImageRequest.bytes()
        );
        return binaryContentRepository.save(profileImage).getId();
    }

    private UserStatus createUserStatus(UUID userId) {
        UserStatus userStatus = new UserStatus(userId, Instant.now());
        return userStatusRepository.save(userStatus);
    }

    private void deleteProfileImage(User user) {
        if (user.getProfileImageId() != null) {
            binaryContentRepository.deleteById(user.getProfileImageId());
        }
    }

    private void deleteAuthoredChannelData(UUID authorId) {
        channelRepository.findByAll().stream()
                .filter(channel -> authorId.equals(channel.getAuthorId()))
                .map(Channel::getId)
                .forEach(this::deleteChannelData);
    }

    private void deleteChannelData(UUID channelId) {
        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
    }

    private void deleteUserData(UUID id) {
        messageRepository.deleteByAuthorId(id);
        channelRepository.deleteByAuthorId(id);
        userStatusRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }
}
