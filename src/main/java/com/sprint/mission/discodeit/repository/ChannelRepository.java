package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel); //저장
    Channel findById(UUID id); //단건 조회
    List<Channel> findAll(); //전체 조회
    void deleteById(UUID id); //단건 삭제
    void deleteByAuthorId(UUID authorId);
}
