package com.sprint.mission.discodeit.entity;

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
public class UserStatus extends Entity{
    private final UUID id;
    private final UUID userId;
    private Instant lastActiveAt; //마지막 활동 시간
    public UserStatus(UUID id, UUID userId) {
        super();
        this.id = id;
        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    //5분 이내 활동 시 온라인으로 간주
    public boolean isOnline(){
        return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

    public void updateLastOnlineAt(Instant lastActiveAt){
        this.lastActiveAt = lastActiveAt;
    }
}
