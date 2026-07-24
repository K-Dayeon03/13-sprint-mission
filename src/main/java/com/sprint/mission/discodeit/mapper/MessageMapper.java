package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageDto toDto(Message message) {
        List<BinaryContent> attachments = message.getAttachments();
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                userMapper.toDto(message.getAuthor()),
                attachments == null ? List.of() : attachments.stream()
                        .map(binaryContentMapper::toDto)
                        .toList()
        );
    }
}
