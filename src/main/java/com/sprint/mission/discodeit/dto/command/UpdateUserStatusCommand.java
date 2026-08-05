package com.sprint.mission.discodeit.dto.command;

import java.time.Instant;

public record UpdateUserStatusCommand(
        Instant newLastActiveAt
) {
}
