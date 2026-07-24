package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

//채널 정보와 최근 메세지 시간,
//private 채널 참여자 정보를 포함하는 응답 DTO
public record ChannelDto(
        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UserDto> participants,
        Instant lastMessageAt
) {}
