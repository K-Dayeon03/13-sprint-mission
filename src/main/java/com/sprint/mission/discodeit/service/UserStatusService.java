package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreateUserStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserStatusCommand;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;

import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(CreateUserStatusCommand command);
    UserStatusResponse findById(UUID id);
//    List<UserStatusResponse> findAll();//유저 상태는 GET /api/users에서 online상태 전체 확인 가능함
    UserStatusResponse update(UUID id, UpdateUserStatusCommand command);
    UserStatusResponse updateByUserId(UUID userId, UpdateUserStatusCommand command);
    void deleteById(UUID id);
}
