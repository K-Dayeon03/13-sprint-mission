package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.exception.InvalidRequestException;
import jakarta.persistence.*;
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
@Entity
@Table(name = "read_statuses")
public class ReadStatus extends BaseUpdatableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;
    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    @Transient
    private UUID userId;

    @Transient
    private UUID channelId;

    protected ReadStatus() {
    }

    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        super();
        if (user == null) throw new InvalidRequestException("유저는 필수입니다.");
        if (channel == null) throw new InvalidRequestException("채널은 필수입니다.");
        this.user = user;
        this.channel = channel;
        this.userId = user.getId();
        this.channelId = channel.getId();
        this.lastReadAt = lastReadAt != null ? lastReadAt : Instant.now();
    }

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        super();
        if (userId == null) throw new InvalidRequestException("유저 아이디는 필수입니다.");
        if (channelId == null) throw new InvalidRequestException("채널 아이디는 필수입니다.");
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt != null ? lastReadAt : Instant.now();
    }

    public UUID getUserId() {
        if (user != null) {
            return user.getId();
        }
        return userId;
    }

    public UUID getChannelId() {
        if (channel != null) {
            return channel.getId();
        }
        return channelId;
    }

    public void updateLastReadAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
