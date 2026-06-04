package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

//    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
//        this.userRepository = userRepository;
//        this.channelRepository = channelRepository;
//        this.messageRepository = messageRepository;
//    }

    @Override
    public User create(String username, String password, String email) {
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            throw new IllegalArgumentException("이름 또는 비밀번호를 작성해주세요.");
        }
        User user = new User(username, password, email);
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findByAll() {
        return userRepository.findByAll();
    }

    @Override
    public User update(UUID id, String currentPassword, String newUsername, String newPassword, String newEmail) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (!user.getPassword().equals(currentPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        user.update(newUsername, newPassword, newEmail);
        return userRepository.save(user);
    }

    @Override
    public void deleteById(UUID id) {
        messageRepository.deleteByAuthorId(id); // 작성한 메시지 삭제
        channelRepository.deleteByAuthorId(id); // 만든 채널 + 채널 메시지 삭제
        userRepository.deleteById(id);
    }
}