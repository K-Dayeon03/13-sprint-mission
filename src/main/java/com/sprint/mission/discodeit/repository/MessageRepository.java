package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    interface ChannelLastMessageAt {
        UUID getChannelId();

        Instant getLastMessageAt();
    }

    @EntityGraph(attributePaths = {"author", "author.profile"})
    Slice<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);

    @Query("""
    select m
    from Message m
    where m.channel.id = :channelId
      and (:cursor is null or m.createdAt < :cursor)
    order by m.createdAt desc
""")
    @EntityGraph(attributePaths = {"author", "author.profile"})
    List<Message> findAllByChannelIdAndCursor(
            @Param("channelId") UUID channelId,
            @Param("cursor") Instant cursor,
            Pageable pageable
    );

    @Query("""
            select m.channel.id as channelId, max(m.createdAt) as lastMessageAt
            from Message m
            where m.channel.id in :channelIds
            group by m.channel.id
            """)
    List<ChannelLastMessageAt> findLastMessageAtByChannelIdIn(@Param("channelIds") Collection<UUID> channelIds);

    List<Message> findByChannel_Id(UUID channelId);
    void deleteByChannel_Id(UUID channelId);
    void deleteByAuthor_Id(UUID authorId);
}
