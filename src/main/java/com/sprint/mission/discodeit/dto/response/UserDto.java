package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

/*
* 패스워드를 제외한 유저 정보와 온라인 상태를 포함하는 응답 DTO입니다.
* */
//service가 바깥으로 데이터를 내보낼때 쓰는 포장지
public record UserDto(
        UUID id,
        String username,
        String email,
        BinaryContentDto profile,
        Boolean online

) {
    public static UserDto from(User user, UserStatus userStatus) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                BinaryContentDto.from(user.getProfile()),
                userStatus != null && userStatus.isOnline()
        );
    }
}
