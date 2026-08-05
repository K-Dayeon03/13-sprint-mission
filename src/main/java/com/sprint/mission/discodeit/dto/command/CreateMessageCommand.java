package com.sprint.mission.discodeit.dto.command;

import java.util.List;
import java.util.UUID;

public record CreateMessageCommand(
        String content,
        UUID channelId,
        UUID authorId,
        List<BinaryContentCommand> attachments
) {
}
