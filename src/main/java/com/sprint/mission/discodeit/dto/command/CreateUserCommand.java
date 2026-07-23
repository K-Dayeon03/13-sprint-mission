package com.sprint.mission.discodeit.dto.command;

public record CreateUserCommand(
        String username,
        String email,
        String password
) {
}
