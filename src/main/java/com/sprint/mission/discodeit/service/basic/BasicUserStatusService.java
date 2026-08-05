package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreateUserStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserStatusCommand;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.InvalidRequestException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusDto create(CreateUserStatusCommand command) {
        User user = findUserOrThrow(command.userId());
        userStatusRepository.findByUser_Id(command.userId())
                .ifPresent(us -> {
                    throw new UserStatusAlreadyExistsException(command.userId());
                });

        UserStatus userStatus = new UserStatus(user, Instant.now());
        return userStatusMapper.toDto(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusDto findById(UUID id) {
        return userStatusMapper.toDto(findEntityOrThrow(id));
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
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public UserStatusDto updateByUserId(UUID userId, UpdateUserStatusCommand command) {
        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() -> UserStatusNotFoundException.byUserId(userId));
        validateUpdateCommand(command);
        userStatus.updateLastActiveAt(command.newLastActiveAt());
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        findEntityOrThrow(id);
        userStatusRepository.deleteById(id);
    }

    private void validateUpdateCommand(UpdateUserStatusCommand command) {
        if (command == null || command.newLastActiveAt() == null) {
            throw new InvalidRequestException("마지막 활동 시간은 필수입니다.");
        }
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserStatus findEntityOrThrow(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new UserStatusNotFoundException(id));
    }
}
