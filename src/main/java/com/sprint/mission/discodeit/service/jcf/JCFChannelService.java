package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final MessageService messageService;
    public JCFChannelService(MessageService messageService) {
        this.data = new HashMap<>();
        this.messageService = messageService;
    }

    @Override
    public Channel create(ChannelType type, String name, String description, UUID authorId) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("채널명을 입력해주세요.");
        }
        Channel channel = new Channel(type, name, description, authorId);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean update(UUID id, ChannelType newType, String newName, String newDescription) {
        Channel channel = data.get(id);
        if (channel == null) {
            return false; // 채널 없으면 false 반환
        }
        channel.update(
                newType != null ? newType : channel.getType(),
                newName != null ? newName : channel.getName(),
                newDescription != null ? newDescription : channel.getDescription()
        );
        return true;
    }

    @Override
    public void delete(UUID id) {
        messageService.deleteByChannelId(id); // 연관 메시지 먼저 삭제
        data.remove(id);                       // 채널 삭제
    }

    // deleteByAuthorId 하나만 남기기
    @Override
    public void deleteByAuthorId(UUID authorId) {
        data.values().stream()
                .filter(c -> c.getAuthorId().equals(authorId))
                .forEach(c -> messageService.deleteByChannelId(c.getId()));
        data.values().removeIf(e -> e.getAuthorId().equals(authorId));

    }
}
