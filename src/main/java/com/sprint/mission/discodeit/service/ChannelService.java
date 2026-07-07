package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.dto.command.UpdateChannelCommand;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;

import java.util.List;
import java.util.UUID;
public interface ChannelService {
    ChannelResponse createPublic(CreatePublicChannelCommand command);
    ChannelResponse createPrivate(CreatePrivateChannelCommand command);
    ChannelResponse findById(UUID id);
    List<ChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse update(UUID id, UpdateChannelCommand command);
    void deleteById(UUID id);
}
