package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.command.LoginCommand;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthCommandMapper {

    public LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.username(), request.password());
    }
}
