package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(CreateUserStatusRequest request) {
        // 유저 존재 여부 확인
        if (userRepository.findById(request.userId()) == null) {
            throw new NotFoundException("존재하지 않는 유저입니다.");
        }
        // 같은 User의 UserStatus 중복 체크
        userStatusRepository.findByUserId(request.userId())
                .ifPresent(us -> {
                    throw new BadRequestException("이미 존재하는 UserStatus입니다.");
                });

        UserStatus userStatus = new UserStatus(request.userId(), Instant.now());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus findById(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id);
        if (userStatus == null) {
            throw new NotFoundException("존재하지 않는 UserStatus입니다.");
        }
        return userStatus;
    }
//
//    @Override
//    public List<UserStatus> findAll() {
//        return userStatusRepository.findAll();
//    }

    @Override
    public UserStatus update(UUID id, UpdateUserStatusRequest request) {
        UserStatus userStatus = userStatusRepository.findById(id);
        if (userStatus == null) {
            throw new NotFoundException("존재하지 않는 UserStatus입니다.");
        }
        validateUpdateRequest(request);
        userStatus.updateLastActiveAt(request.newLastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UpdateUserStatusRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 UserStatus입니다."));
        validateUpdateRequest(request);
        userStatus.updateLastActiveAt(request.newLastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public void deleteById(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id);
        if (userStatus == null) {
            throw new NotFoundException("존재하지 않는 UserStatus입니다.");
        }
        userStatusRepository.deleteById(id);
    }

    private void validateUpdateRequest(UpdateUserStatusRequest request) {
        if (request == null || request.newLastActiveAt() == null) {
            throw new BadRequestException("마지막 활동 시간은 필수입니다.");
        }
    }
}
