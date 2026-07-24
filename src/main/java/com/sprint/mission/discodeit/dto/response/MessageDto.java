package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        UserDto author,
        List<BinaryContentDto> attachments
) {
    public static MessageDto from(Message message) {
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                UserDto.from(message.getAuthor(), message.getAuthor() != null ? message.getAuthor().getUserStatus() : null),
                message.getAttachments().stream()
                        .map(BinaryContentDto::from)
                        .toList()
        );
    }
}
