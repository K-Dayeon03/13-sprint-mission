package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.command.CreateUserCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserCommand;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;


    @Override
    @Transactional
    public UserDto create(CreateUserCommand command, BinaryContentCommand profileImageCommand) {
        validateUsernameAndEmail(command.username(), command.email());

        User user = new User(command.username(), command.password(), command.email(), null);
        saveUserWithProfileImage(user, profileImageCommand);

        UserStatus userStatus = createUserStatus(user);
        return UserDto.from(user, userStatus);
    }

    @Override
    @Transactional
    public UserDto findById(UUID id) {
        User user = findUserOrThrow(id);
        UserStatus userStatus = getOrCreateUserStatus(id);
        return UserDto.from(user, userStatus);
    }
    @Override
    @Transactional
    public List<UserDto> findByAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = getOrCreateUserStatus(user.getId());
                    return UserDto.from(user, userStatus);
                })
                .toList();
    }

    @Override
    @Transactional
    public UserDto update(UUID id, UpdateUserCommand command,
                          BinaryContentCommand profileImageCommand) {
        User user = findUserOrThrow(id);
        validateUpdatedUsernameAndEmail(id, command);

        BinaryContent newProfileImage = null;
        if (profileImageCommand != null) {
            newProfileImage = new BinaryContent(
                    null,
                    null,
                    profileImageCommand.fileName(),
                    profileImageCommand.contentType(),
                    profileImageCommand.bytes()
            );
        }

        user.update(command.newUsername(), command.newPassword(),
                command.newEmail(), newProfileImage);

        UserStatus userStatus = getOrCreateUserStatus(id);
        return UserDto.from(user, userStatus);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        findUserOrThrow(id);
        deleteAuthoredChannelData(id);
        deleteUserData(id);
    }

    private void validateUsernameAndEmail(String username, String email) {
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw new BadRequestException("이미 사용 중인 유저 이름 또는 이메일 입니다.");
        }
    }

    private void validateUpdatedUsernameAndEmail(UUID userId, UpdateUserCommand command) {
        String newUsername = command.newUsername();
        String newEmail = command.newEmail();
        if (newUsername == null && newEmail == null) {
            return;
        }

        if (newUsername != null && userRepository.existsByUsernameAndIdNot(newUsername, userId)) {
            throw new BadRequestException("이미 사용 중인 유저 이름입니다.");
        }
        if (newEmail != null && userRepository.existsByEmailAndIdNot(newEmail, userId)) {
            throw new BadRequestException("이미 사용 중인 이메일입니다.");
        }
    }

    private void saveUserWithProfileImage(User user, BinaryContentCommand profileImageCommand) {
        if (profileImageCommand != null) {
            BinaryContent profileImage = new BinaryContent(
                    null,
                    null,
                    profileImageCommand.fileName(),
                    profileImageCommand.contentType(),
                    profileImageCommand.bytes()
            );
            user.update(null, null, null, profileImage);
        }
        userRepository.save(user);
    }

    private User findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
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
