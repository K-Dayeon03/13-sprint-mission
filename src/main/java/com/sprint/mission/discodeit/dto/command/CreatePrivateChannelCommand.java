package com.sprint.mission.discodeit.dto.command;

import java.util.List;
import java.util.UUID;

public record CreatePrivateChannelCommand(
        String name,
        String description,
        List<UUID> participantIds
) {
}
