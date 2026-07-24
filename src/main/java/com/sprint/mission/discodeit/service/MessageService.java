package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.command.UpdateMessageCommand;
import com.sprint.mission.discodeit.dto.response.MessageDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(CreateMessageCommand command);
    MessageDto findById(UUID id);
    List<MessageDto> findAllByChannelId(UUID channelId);
    MessageDto update(UUID id, UpdateMessageCommand command);
    void deleteById(UUID id);
}
