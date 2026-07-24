package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    Slice<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);
    List<Message> findByChannel_Id(UUID channelId);
    void deleteByChannel_Id(UUID channelId);
    void deleteByAuthor_Id(UUID authorId);
}
