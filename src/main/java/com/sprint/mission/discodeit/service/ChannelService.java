package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelType type, String name, String description, UUID authorId);
    Channel findById(UUID id);
    List<Channel> findAll();
    boolean update(UUID id, ChannelType newType, String newName, String newDescription);
    void delete(UUID id);
    void deleteByAuthorId(UUID authorId);

}
