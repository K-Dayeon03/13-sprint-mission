package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
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
    public Channel create(ChannelType type, String name, String description) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("채널명을 입력해주세요.");
        }
        Channel channel = new Channel(type, name, description);
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
    public void update(UUID id, ChannelType type, ChannelType newType, String newName, String newDescription) {
        Channel channel = data.get(id);
        if(channel == null){
            throw new IllegalArgumentException("존재하지 않은 채널입니다. 채널 아이디: " + id);
        }

        //새로운 값이 있을 때만 업데이트
        channel.update(
                newType != null ? newType : channel.getType(),
                newName != null ? newName : channel.getName(),
                newDescription != null ? newDescription : channel.getDescription()
        );
    }


    @Override
    public void delete(UUID id) {
        messageService.deleteByChannelId(id); // 연관 메시지 먼저 삭제
        data.remove(id);                       // 채널 삭제
    }
}
