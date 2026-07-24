package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final BinaryContentMapper binaryContentMapper;

    public UserDto toDto(User user) {
        return toDto(user, user != null ? user.getUserStatus() : null);
    }

    public UserDto toDto(User user, UserStatus userStatus) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                binaryContentMapper.toDto(user.getProfile()),
                userStatus != null && userStatus.isOnline()
        );
    }
}
