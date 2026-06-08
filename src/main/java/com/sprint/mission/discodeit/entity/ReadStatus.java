package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;
//사용자가 채널 별 마지막으로 메세지를 읽은 시간을 표현
//사용자별 각 채널에 읽지 않은 메세지를 확인
/**
 * 특정 유저가 특정 채널의 메시지를 마지막으로 읽은 시간을 나타내는 도메인 모델입니다.
 * PRIVATE 채널의 참여자 정보를 관리하기 위해 활용됩니다.
 */
@Getter
public class ReadStatus extends Entity{
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        super();
        if (userId == null) throw new IllegalArgumentException("유저 아이디는 필수입니다.");
        if (channelId == null) throw new IllegalArgumentException("채널 아이디는 필수입니다.");
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt != null ? lastReadAt : Instant.now();
    }
    public void updateLastReadAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        makeUpdate();
    }
}
