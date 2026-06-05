package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;
//사용자가 채널 별 마지막으로 메세지를 읽은 시간을 표현
//사용자별 각 채널에 읽지 않은 메세지를 확인
@Getter
public class ReadStatus extends Entity{
    private UUID id;
    private UUID userId;
    private UUID channelId;

    public ReadStatus(UUID id, UUID userId, UUID channelId) {
        super();
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
    }
}
