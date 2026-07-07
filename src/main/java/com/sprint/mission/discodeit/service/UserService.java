package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.command.CreateUserCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserCommand;
import com.sprint.mission.discodeit.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(CreateUserCommand command, BinaryContentCommand profileImageCommand);
    UserResponse findById(UUID id);
    List<UserResponse> findByAll();
    UserResponse update(UUID id, UpdateUserCommand command, BinaryContentCommand profileImageCommand);
    void deleteById(UUID id);
}
