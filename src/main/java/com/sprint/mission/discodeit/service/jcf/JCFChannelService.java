package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageService messageService;

    public JCFChannelService(ChannelRepository channelRepository, MessageService messageService) {
        this.channelRepository = channelRepository;
        this.messageService = messageService;
    }

    @Override
    public Channel create(ChannelType type, String name, String description, UUID authorId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널명을 입력해주세요.");
        }
        Channel channel = new Channel(type, name, description, authorId);
        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> findByAll() {
        return channelRepository.findByAll();
    }

    @Override
    public Channel update(UUID id, ChannelType newType, String newName, String newDescription) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        channel.update(
                newType != null ? newType : channel.getType(),
                newName != null ? newName : channel.getName(),
                newDescription != null ? newDescription : channel.getDescription()
        );
        return channelRepository.save(channel);
    }

    @Override
    public void deleteById(UUID id) {
        messageService.deleteByChannelId(id); // 연관 메시지 먼저 삭제
        channelRepository.deleteById(id);
    }

    @Override
    public void deleteByAuthorId(UUID authorId) {
        channelRepository.findByAll().stream()
                .filter(c -> c.getAuthorId().equals(authorId))
                .forEach(c -> {
                    messageService.deleteByChannelId(c.getId()); // 연관 메시지 먼저 삭제
                    channelRepository.deleteById(c.getId());
                });
    }
}