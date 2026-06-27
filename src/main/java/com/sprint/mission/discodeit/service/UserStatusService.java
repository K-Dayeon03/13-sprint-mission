package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(CreateUserStatusRequest request);
    UserStatus findById(UUID id);
//    List<UserStatus> findAll();//유저 상태는 GET /api/users에서 online상태 전체 확인 가능함
    UserStatus update(UUID id, UpdateUserStatusRequest request);
    UserStatus updateByUserId(UUID userId, UpdateUserStatusRequest request);
    void deleteById(UUID id);
}