package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;
// ChannelService.java 인터페이스
public interface ChannelService {
    ChannelResponse createPublic(CreatePublicChannelRequest request);
    ChannelResponse createPrivate(CreatePrivateChannelRequest request);
    ChannelResponse findById(UUID id);
    List<ChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse update(UUID id, UpdateChannelRequest request);
    void deleteById(UUID id);
}