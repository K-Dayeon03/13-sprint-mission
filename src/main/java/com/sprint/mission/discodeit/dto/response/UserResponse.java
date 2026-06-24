package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

/*
* 패스워드를 제외한 유저 정보와 온라인 상태를 포함하는 응답 DTO입니다.
* */
//service가 바깥으로 데이터를 내보낼때 쓰는 포장지
public record UserResponse(
        UUID id,
        String username,
        String email,
        UUID profileId,
        boolean online,
        Instant createdAt,
        Instant updatedAt

) {
    //UserStatus로 온라인 여부 판단해서 생성
    public static UserResponse from(User user, UserStatus userStatus){
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                userStatus.isOnline(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
