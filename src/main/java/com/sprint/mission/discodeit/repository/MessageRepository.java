package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface MessageRepository {
    Message save(Message message); //저장
    Message findById(UUID id);  //단건 조회
    List<Message> findByAll(); //전체 조회
    List<Message> findByChannelId(UUID channelId); //채널별 조회
    void deleteById(UUID id); //단건 삭제
    void deleteByChannelId(UUID channelId); //채널 기준 삭제
    void deleteByAuthorId(UUID authorId); //작성자 기준 삭제
}
