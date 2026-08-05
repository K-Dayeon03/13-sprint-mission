package com.sprint.mission.discodeit.dto.command;

public record UpdateChannelCommand(
        String newName,
        String newDescription
) {
}
