package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreateUserStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateUserStatusCommand;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;

import java.util.UUID;

public interface UserStatusService {
    UserStatusDto create(CreateUserStatusCommand command);
    UserStatusDto findById(UUID id);
//    List<UserStatusDto> findAll();//유저 상태는 GET /api/users에서 online상태 전체 확인 가능함
    UserStatusDto update(UUID id, UpdateUserStatusCommand command);
    UserStatusDto updateByUserId(UUID userId, UpdateUserStatusCommand command);
    void deleteById(UUID id);
}
