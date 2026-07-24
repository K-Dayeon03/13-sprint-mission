package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.dto.command.UpdateChannelCommand;
import com.sprint.mission.discodeit.dto.response.ChannelDto;

import java.util.List;
import java.util.UUID;
public interface ChannelService {
    ChannelDto createPublic(CreatePublicChannelCommand command);
    ChannelDto createPrivate(CreatePrivateChannelCommand command);
    ChannelDto findById(UUID id);
    List<ChannelDto> findAllByUserId(UUID userId);
    ChannelDto update(UUID id, UpdateChannelCommand command);
    void deleteById(UUID id);
}
