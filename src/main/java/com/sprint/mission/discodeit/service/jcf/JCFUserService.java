package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
//JCF활용

//연관 삭제위해 추가
public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    private final ChannelService channelService;
    private final MessageService messageService;

    public JCFUserService(ChannelService channelService, MessageService messageService) {
        this.channelService = channelService;
        this.messageService = messageService;
        this.data = new HashMap<>();
    }


    @Override
    public User create(String username, String password, String email) {
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            throw new IllegalArgumentException("이름 또는 비밀번호를 작성해주세요.");
        }

        User user = new User(username, password, email);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean update(UUID id, String currentPassword, String newUsername, String newPassword, String newEmail) {
        //먼저 유저 조회
        User user = data.get(id);

        //존재하는지 확인
        if (user == null) {
            System.out.println("존재하지 않는 사용자입니다.");
            return false;
        }
        //비밀번호가 일치 않으면 실패
        if (!user.getPassword().equals(currentPassword)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return false;
        }
        //모두 일치일 경우
        user.update(newUsername, newPassword, newEmail);
        return true;
    }

    @Override
    public void delete(UUID id) {
        //유저 삭제 시 -> 작성한 메세지와 채널을 삭제해야된다.
        messageService.deleteByAuthorId(id);
        channelService.deleteByAuthorId(id);
        data.remove(id);

    }
}