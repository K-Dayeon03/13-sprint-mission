package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    Optional<ReadStatus> findByUser_IdAndChannel_Id(UUID userId, UUID channelId);
    List<ReadStatus> findAllByUser_Id(UUID userId);
    List<ReadStatus> findAllByChannel_Id(UUID channelId);

    @EntityGraph(attributePaths = {"user", "user.profile", "user.userStatus"})
    List<ReadStatus> findAllByChannel_IdIn(Collection<UUID> channelIds);

    void deleteByUser_Id(UUID userId);
    void deleteByChannel_Id(UUID channelId);
}
