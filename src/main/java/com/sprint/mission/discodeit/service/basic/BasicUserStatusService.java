package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreateUserStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserStatusCommand;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(CreateUserStatusCommand command) {
        Optional.ofNullable(userRepository.findById(command.userId()))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        userStatusRepository.findByUserId(command.userId())
                .ifPresent(us -> {
                    throw new BadRequestException("이미 존재하는 UserStatus입니다.");
                });

        UserStatus userStatus = new UserStatus(command.userId(), Instant.now());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus findById(UUID id) {
        return Optional.ofNullable(userStatusRepository.findById(id))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 UserStatus입니다."));
    }
//
//    @Override
//    public List<UserStatus> findAll() {
//        return userStatusRepository.findAll();
//    }

    @Override
    public UserStatus update(UUID id, UpdateUserStatusCommand command) {
        UserStatus userStatus = findById(id);
        validateUpdateCommand(command);
        userStatus.updateLastActiveAt(command.newLastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UpdateUserStatusCommand command) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 UserStatus입니다."));
        validateUpdateCommand(command);
        userStatus.updateLastActiveAt(command.newLastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        userStatusRepository.deleteById(id);
    }

    private void validateUpdateCommand(UpdateUserStatusCommand command) {
        if (command == null || command.newLastActiveAt() == null) {
            throw new BadRequestException("마지막 활동 시간은 필수입니다.");
        }
    }
}
