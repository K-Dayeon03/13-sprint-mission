package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.command.UpdateMessageCommand;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;

import java.time.Instant;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(CreateMessageCommand command);
    MessageResponse findById(UUID id);
    PageResponse<MessageResponse> findAllByChannelId(UUID channelId, Instant cursor, int size);    MessageResponse update(UUID id, UpdateMessageCommand command);
    void deleteById(UUID id);
}
