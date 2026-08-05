package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.command.CreateUserStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserStatusCommand;
import com.sprint.mission.discodeit.dto.request.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import org.springframework.stereotype.Component;

@Component
public class UserStatusCommandMapper {

    public CreateUserStatusCommand toCreateCommand(CreateUserStatusRequest request) {
        return new CreateUserStatusCommand(request.userId());
    }

    public UpdateUserStatusCommand toUpdateCommand(UpdateUserStatusRequest request) {
        return new UpdateUserStatusCommand(request.newLastActiveAt());
    }
}
