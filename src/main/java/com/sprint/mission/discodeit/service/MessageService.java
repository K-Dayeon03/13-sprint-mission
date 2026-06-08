package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
        Message create(CreateMessageRequest request);
        Message findById(UUID id);
        List<Message> findAllByChannelId(UUID channelId);
        Message update(UUID id, UpdateMessageRequest request); // 추가
        void deleteById(UUID id);

}
