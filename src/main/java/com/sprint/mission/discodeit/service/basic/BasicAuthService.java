package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.LoginCommand;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.auth.AuthenticationFailedException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto login(LoginCommand command) {
        User user = userRepository.findByUsername(command.username())
                .filter(u -> u.getPassword().equals(command.password()))
                .orElseThrow(() -> new AuthenticationFailedException(command.username()));

        UserStatus userStatus = userStatusRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> UserStatusNotFoundException.byUserId(user.getId()));

        userStatus.updateLastActiveAt(Instant.now());

        return userMapper.toDto(user, userStatus);
    }
}
