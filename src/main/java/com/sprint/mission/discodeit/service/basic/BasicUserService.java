package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.command.CreateUserCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserCommand;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserStatusRepository userStatusRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public UserDto create(CreateUserCommand command, BinaryContentCommand profileImageCommand) {
        log.debug("Creating user. username={}, email={}, hasProfileImage={}",
                command.username(), command.email(), profileImageCommand != null);

        validateUsernameAndEmail(command.username(), command.email());
        User user = new User(command.username(), command.password(), command.email(), null);
        applyProfileImage(user, profileImageCommand);
        User saved = userRepository.saveAndFlush(user);
        saveProfileImageBytes(saved, profileImageCommand);

        UserStatus userStatus = createUserStatus(user);
        log.info("User created. userId={}, username={}", saved.getId(), saved.getUsername());

        return userMapper.toDto(saved, userStatus);
    }

    @Override
    @Transactional
    public UserDto findById(UUID id) {
        User user = findUserOrThrow(id);
        UserStatus userStatus = getOrCreateUserStatus(id);
        return userMapper.toDto(user, userStatus);
    }
    @Override
    @Transactional
    public List<UserDto> findByAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = getOrCreateUserStatus(user.getId());
                    return userMapper.toDto(user, userStatus);
                })
                .toList();
    }

    @Override
    @Transactional
    public UserDto update(UUID id, UpdateUserCommand command,
                          BinaryContentCommand profileImageCommand) {
        User user = findUserOrThrow(id);
        log.debug("Updating user. userId={}, hasProfileImage={}", id, profileImageCommand != null);
        validateUpdatedUsernameAndEmail(id, command);

        BinaryContent newProfileImage = createProfileImage(profileImageCommand);

        user.update(command.newUsername(), command.newPassword(),
                command.newEmail(), newProfileImage);
        if (profileImageCommand != null) {
            userRepository.saveAndFlush(user);
            saveProfileImageBytes(user, profileImageCommand);
        }

        UserStatus userStatus = getOrCreateUserStatus(id);
        log.info("User updated. userId={}", id);
        return userMapper.toDto(user, userStatus);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        findUserOrThrow(id);
        log.debug("Deleting user. userId={}", id);
        deleteAuthoredChannelData(id);
        deleteUserData(id);
        log.info("User deleted. userId={}", id);
    }

    private void validateUsernameAndEmail(String username, String email) {
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            log.warn("User creation failed. duplicated username or email. username={}", username);
            throw new UserAlreadyExistsException(username);
        }
    }

    private void validateUpdatedUsernameAndEmail(UUID userId, UpdateUserCommand command) {
        String newUsername = command.newUsername();
        String newEmail = command.newEmail();
        if (newUsername == null && newEmail == null) {
            return;
        }

        if (newUsername != null && userRepository.existsByUsernameAndIdNot(newUsername, userId)) {
            throw new UserAlreadyExistsException(newUsername);
        }
        if (newEmail != null && userRepository.existsByEmailAndIdNot(newEmail, userId)) {
            throw UserAlreadyExistsException.byEmail(newEmail);
        }
    }

    private void applyProfileImage(User user, BinaryContentCommand profileImageCommand) {
        BinaryContent profileImage = createProfileImage(profileImageCommand);
        if (profileImage != null) {
            user.update(null, null, null, profileImage);
        }
    }

    private BinaryContent createProfileImage(BinaryContentCommand profileImageCommand) {
        if (profileImageCommand != null) {
            return new BinaryContent(
                    null,
                    null,
                    profileImageCommand.fileName(),
                    profileImageCommand.contentType(),
                    (long) profileImageCommand.bytes().length
            );
        }
        return null;
    }

    private void saveProfileImageBytes(User user, BinaryContentCommand profileImageCommand) {
        if (profileImageCommand != null && user.getProfile() != null) {
            binaryContentStorage.put(user.getProfile().getId(), profileImageCommand.bytes());
        }
    }

    private User findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private UserStatus createUserStatus(User user) {
        UserStatus userStatus = new UserStatus(user, Instant.now());
        return userStatusRepository.save(userStatus);
    }

    private UserStatus getOrCreateUserStatus(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
                .orElseGet(() -> createUserStatus(findUserOrThrow(userId)));
        if (userStatus.getLastActiveAt() == null) {
            userStatus.updateLastActiveAt(Instant.now());
        }
        return userStatus;
    }

    private void deleteAuthoredChannelData(UUID authorId) {
        channelRepository.findAll().stream()
                .filter(channel -> authorId.equals(channel.getAuthorId()))
                .map(Channel::getId)
                .forEach(this::deleteChannelData);
    }

    private void deleteChannelData(UUID channelId) {
        MessageDeletionSupport.deleteByChannelId(messageRepository, binaryContentRepository, channelId);
        readStatusRepository.deleteByChannel_Id(channelId);
    }

    private void deleteUserData(UUID id) {
        MessageDeletionSupport.deleteByAuthorId(messageRepository, binaryContentRepository, id);
        readStatusRepository.deleteByUser_Id(id);
        userStatusRepository.deleteByUser_Id(id);
        userRepository.deleteById(id);
    }
}
