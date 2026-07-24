package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.command.UpdateMessageCommand;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(CreateMessageCommand command);
    MessageDto findById(UUID id);
    PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page);
    MessageDto update(UUID id, UpdateMessageCommand command);
    void deleteById(UUID id);
}
