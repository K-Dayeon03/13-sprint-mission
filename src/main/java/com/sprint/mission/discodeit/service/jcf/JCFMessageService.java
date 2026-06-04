package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private ChannelService channelService;
    private UserService userService;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    public void init(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (channelService.findById(channelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        if (userService.findById(authorId) == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        if (content == null || content.isBlank()) {
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

    @Override
    public List<Message> findByAll() {
        return new ArrayList<>(data.values());
    }


    public List<Message> findByAll(UUID channelId, UUID authorId) {
        return data.values().stream()
                .filter(m -> m.getChannelId().equals(channelId)
                        && m.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }
    @Override
    public boolean update(UUID id, String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("메세지 내용을 입력해주세요.");
        }

        Message message = data.get(id);

        if (message == null) {
            return false;  // 메시지 없으면 false 반환
        }

        message.update(newContent);
        return true;  // 성공 시 true 반환
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
