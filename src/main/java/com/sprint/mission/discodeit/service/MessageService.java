package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.command.UpdateMessageCommand;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(CreateMessageCommand command);
    Message findById(UUID id);
    List<Message> findAllByChannelId(UUID channelId);
    Message update(UUID id, UpdateMessageCommand command);
    void deleteById(UUID id);
}
