package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface UserRepository {
    User save(User user);           // 저장 (등록/수정 공통)
    User findById(UUID id);         // 단건 조회
    List<User> findByAll();           // 전체 조회
    void deleteById(UUID id);       // 삭제
}
