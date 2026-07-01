package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    ReadStatus findById(UUID id);
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId); // 중복 체크용
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    void deleteById(UUID id);
    void deleteByChannelId(UUID channelId);
    void deleteByUserId(UUID userId);
}
