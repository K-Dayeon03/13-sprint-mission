package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

//사용자별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델
//사용자의 온라인 상태 확인
/*
* 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드
* 마지막 접속 시간으로부터 5분 이내이면서 현재 접속 중인 유저로 간주
* */
@Getter
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Transient
    private UUID userId;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt; //마지막 활동 시간

    protected UserStatus() {

    }

    public UserStatus(User user, Instant lastActiveAt) {
        super();
        if (user == null) {
            throw new IllegalArgumentException("유저는 필수입니다.");
        }
        this.user = user;
        this.userId = user.getId();
        this.lastActiveAt = lastActiveAt != null ? lastActiveAt : Instant.now();

    }

    public UserStatus(UUID userId, Instant lastActiveAt) {
        super();
        if (userId == null) {
            throw new IllegalArgumentException("유저 아이디는 필수 입니다.");
        }
        this.userId = userId;
        this.lastActiveAt = lastActiveAt != null ? lastActiveAt : Instant.now();
    }

    public UUID getUserId() {
        if (user != null) {
            return user.getId();
        }
        return userId;
    }

    //5분 이내 활동 시 온라인으로 간주
    public boolean isOnline(){
        if (lastActiveAt == null) {
            return false;
        }
        return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

    public void updateLastActiveAt(Instant lastActiveAt) {
        if (lastActiveAt == null) {
            throw new IllegalArgumentException("마지막 활동 시간은 필수입니다.");
        }
        this.lastActiveAt = lastActiveAt;
    }
}
