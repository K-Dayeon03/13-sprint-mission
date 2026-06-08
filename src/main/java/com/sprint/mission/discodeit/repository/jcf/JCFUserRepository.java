package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {

    //저장
    private final Map<UUID, User> data = new HashMap<>();
    @Override
    public User save(User user) {
        //등록/수정 공통 - id기준으로 덮어씀
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findByAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteByUserId(UUID id) {
        data.remove(id);
    }
}
