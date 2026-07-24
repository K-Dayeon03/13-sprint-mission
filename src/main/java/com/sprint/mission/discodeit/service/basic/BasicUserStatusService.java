package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreateUserStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserStatusCommand;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserStatusDto create(CreateUserStatusCommand command) {
        User user = findUserOrThrow(command.userId());
        userStatusRepository.findByUser_Id(command.userId())
                .ifPresent(us -> {
                    throw new BadRequestException("이미 존재하는 UserStatus입니다.");
                });

        UserStatus userStatus = new UserStatus(user, Instant.now());
        return UserStatusDto.from(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusDto findById(UUID id) {
        return UserStatusDto.from(findEntityOrThrow(id));
    }
//
//    @Override
//    public List<UserStatus> findAll() {
//        return userStatusRepository.findAll();
//    }

    @Override
    @Transactional
    public UserStatusDto update(UUID id, UpdateUserStatusCommand command) {
        UserStatus userStatus = findEntityOrThrow(id);
        validateUpdateCommand(command);
        userStatus.updateLastActiveAt(command.newLastActiveAt());
        return UserStatusDto.from(userStatus);
    }

    @Override
    @Transactional
    public UserStatusDto updateByUserId(UUID userId, UpdateUserStatusCommand command) {
        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 UserStatus입니다."));
        validateUpdateCommand(command);
        userStatus.updateLastActiveAt(command.newLastActiveAt());
        return UserStatusDto.from(userStatus);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        findEntityOrThrow(id);
        userStatusRepository.deleteById(id);
    }

    private void validateUpdateCommand(UpdateUserStatusCommand command) {
        if (command == null || command.newLastActiveAt() == null) {
            throw new BadRequestException("마지막 활동 시간은 필수입니다.");
        }
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
    }

    private UserStatus findEntityOrThrow(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 UserStatus입니다."));
    }
}
