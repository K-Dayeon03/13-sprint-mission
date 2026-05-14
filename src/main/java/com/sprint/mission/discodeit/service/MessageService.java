package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String content, UUID channelId, UUID authorId);
    List<Message> findByChannelId(UUID channelId);
    Message findById(UUID id);
    void delete(UUID id);
    void deleteByChannelId(UUID channelId);
    void deleteByAuthorId(UUID authorId);

}
