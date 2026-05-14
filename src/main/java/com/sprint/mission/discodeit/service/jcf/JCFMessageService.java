package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if(channelId == null || authorId == null){
            throw new IllegalArgumentException("채널 또는 작성자 아이디가 존재하지 않습니다.");
        }
        if(content == null || content.isBlank()){
            throw new IllegalArgumentException("메세지 내용을 입력해주세요.");
        }
        Message message = new Message(content, channelId, authorId);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }
    public List<Message> findByAll(UUID channelId, UUID authorId) {
        return data.values().stream()
                .filter(m -> m.getChannelId().equals(channelId)
                        && m.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }
    @Override
    public void update(UUID id, String newContent) {
        Message message = data.get(id);
        if (message != null) {
            message.update(newContent);
        }
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.values().removeIf(m -> m.getChannelId().equals(channelId));
    }

    @Override
    public void deleteByAuthorId(UUID authorId) {
        data.values().removeIf(m -> m.getAuthorId().equals(authorId));
    }
}
