package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(ChannelRepository channelRepository,  MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Channel create(ChannelType type, String name, String description, UUID authorId) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("채널명을 입력해주세요.");
        }
        Channel channel = new Channel(type, name, description, authorId);
        return  channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public boolean update(UUID id, ChannelType newType, String newName, String newDescription) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            return false; // 채널 없으면 false 반환
        }
        channel.update(
                newType != null ? newType : channel.getType(),
                newName != null ? newName : channel.getName(),
                newDescription != null ? newDescription : channel.getDescription()
        );
        channelRepository.save(channel);
        return true;
    }

    @Override
    public void delete(UUID id) {
        messageRepository.deleteByChannelId(id);
        channelRepository.deleteById(id);
    }

    @Override
    public void deleteByAuthorId(UUID authorId) {
        //해당 유저가 만든 채 조회 후  메세지 삭제
        channelRepository.findAll().stream()
            .filter(c -> c.getAuthorId().equals(authorId))
            .forEach(c -> messageRepository.deleteByChannelId(c.getId()));
        channelRepository.deleteByAuthorId(authorId);
        }
    }