package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);
    UserStatus findById(UUID id);
    Optional<UserStatus> findByUserId(UUID userId);
    List<UserStatus> findAll();         // 추가
    void deleteById(UUID id);
    void deleteByUserId(UUID userId);
}