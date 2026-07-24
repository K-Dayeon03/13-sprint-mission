package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.command.CreateUserCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserCommand;
import com.sprint.mission.discodeit.dto.response.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto create(CreateUserCommand command, BinaryContentCommand profileImageCommand);
    UserDto findById(UUID id);
    List<UserDto> findByAll();
    UserDto update(UUID id, UpdateUserCommand command, BinaryContentCommand profileImageCommand);
    void deleteById(UUID id);
}
