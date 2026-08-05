package com.sprint.mission.discodeit.dto.command;

public record UpdateUserCommand(
        String newUsername,
        String newEmail,
        String newPassword
) {
}
