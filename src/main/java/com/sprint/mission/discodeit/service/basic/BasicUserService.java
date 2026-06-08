package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
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


    @Override
    public UserResponse create(CreateUserRequest userRequest, CreateBinaryContentRequest profileImageRequest) {
       //username, email 중복 체크
        boolean isDuplicated = userRepository.findByAll().stream()
                .anyMatch(u -> u.getUsername().equals(userRequest.username())
                        || u.getEmail().equals(userRequest.email()));
        if(isDuplicated) {
            throw new IllegalArgumentException("이미 사용 중인 유저 이름 또는 이메일 입니다.");
        }
        //유저 먼저 저장(profileImageId는 나중에 반영)
        User user = new User(userRequest.username(), userRequest.password(),
                userRequest.email(), null);
        userRepository.save(user);

        //프로필 이미지 선택적 저장
        if(profileImageRequest != null){
            BinaryContent profileImage = new BinaryContent(
                    user.getId(), // 유저 아이디 설정
                    null, //메세지 아이디는 널
                    profileImageRequest.fileName(),
                    profileImageRequest.contentType(),
                    profileImageRequest.bytes()
            );
            UUID profileImageId = binaryContentRepository.save(profileImage).getId();
            user.update(null,null,null, profileImageId);
            userRepository.save(user);
        }
        //UserStatus 같이 생성
        UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(userStatus);
        return UserResponse.from(user, userStatus);
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        UserStatus userStatus = userStatusRepository.findByUserId(id);
        return UserResponse.from(user, userStatus);
    }

    @Override
    public List<UserResponse> findByAll() {
        return userRepository.findByAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId());
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

        UserStatus userStatus = userStatusRepository.findByUserId(id);
        return UserResponse.from(user, userStatus);
    }

    @Override
    public void deleteById(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // BinaryContent(프로필) 삭제
        if (user.getProfileImageId() != null) {
            binaryContentRepository.deleteById(user.getProfileImageId());
        }
        // UserStatus 삭제
        userStatusRepository.deleteById(id);
        // 유저 삭제
        userRepository.deleteByUserId(id);
    }
}
