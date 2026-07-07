package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreateUserStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserStatusCommand;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface UserStatusService {
    UserStatus create(CreateUserStatusCommand command);
    UserStatus findById(UUID id);
//    List<UserStatus> findAll();//유저 상태는 GET /api/users에서 online상태 전체 확인 가능함
    UserStatus update(UUID id, UpdateUserStatusCommand command);
    UserStatus updateByUserId(UUID userId, UpdateUserStatusCommand command);
    void deleteById(UUID id);
}
