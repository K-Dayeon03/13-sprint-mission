package com.sprint.mission.discodeit.dto.command;

import java.util.UUID;

public record CreateUserStatusCommand(
        UUID userId
) {
}
