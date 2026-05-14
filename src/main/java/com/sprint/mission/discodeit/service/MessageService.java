package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
        Message create(String content, UUID channelId, UUID authorId);
        Message findById(UUID id);
        List<Message> findByChannelId(UUID channelId);
        void update(UUID id, String newContent);  // 추가
        void delete(UUID id);
        void deleteByChannelId(UUID channelId);
        void deleteByAuthorId(UUID authorId);
}
