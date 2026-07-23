package com.sprint.mission.discodeit.dto.command;

public record CreatePublicChannelCommand(
        String name,
        String description
) {
}
