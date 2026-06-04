package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
public class FileUserService implements UserService {

    private static final Path FILE_PATH = Paths.get("data/users.ser");

    //
    private final ChannelService channelService;
    private final MessageService messageService;

    // 필드에 저장
    public FileUserService(ChannelService channelService, MessageService messageService) {
        this.channelService = channelService;
        this.messageService = messageService;
    }

    private Map<UUID, User> loadData() {
        if (!Files.exists(FILE_PATH)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_PATH.toFile()))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, User> data) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(FILE_PATH.toFile()))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    @Override
    public User create(String username, String password, String email) {
        Map<UUID, User> data = loadData();
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            throw new IllegalArgumentException("이름 또는 비밀번호를 작성해주세요.");
        }
        User user = new User(username, password, email);
        data.put(user.getId(), user);
        saveData(data);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return loadData().get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public boolean update(UUID id, String currentPassword, String newUsername, String newPassword, String newEmail) {
        Map<UUID, User> data = loadData();
        User user = data.get(id);

        if (user == null) return false;
        if (!user.getPassword().equals(currentPassword)) return false;

        user.update(newUsername, newPassword, newEmail);
        saveData(data);
        return true;
    }

    @Override
    public void delete(UUID id) {
        // ✅ 연관 데이터 먼저 삭제
        messageService.deleteByAuthorId(id);  // 작성한 메시지 삭제
        channelService.deleteByAuthorId(id);  // 만든 채널 + 채널 메시지 삭제
        Map<UUID, User> data = loadData();
        data.remove(id);
        saveData(data);
    }
}