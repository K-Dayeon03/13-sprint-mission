package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.command.CreateUserCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserCommand;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import org.springframework.stereotype.Component;

@Component
public class UserCommandMapper {

    public CreateUserCommand toCreateCommand(CreateUserRequest request) {
        return new CreateUserCommand(request.username(), request.email(), request.password());
    }

    public CreateUserCommand toCreateCommand(String username, String email, String password) {
        return new CreateUserCommand(username, email, password);
    }

    public UpdateUserCommand toUpdateCommand(UpdateUserRequest request) {
        return new UpdateUserCommand(request.newUsername(), request.newEmail(), request.newPassword());
    }

    public UpdateUserCommand toUpdateCommand(String newUsername, String newEmail, String newPassword) {
        return new UpdateUserCommand(newUsername, newEmail, newPassword);
    }
}
