package com.sprint.mission.discodeit.dto.command;

import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusCommand(
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
    public CreateReadStatusCommand(UUID userId, UUID channelId) {
        this(userId, channelId, null);
    }
}
