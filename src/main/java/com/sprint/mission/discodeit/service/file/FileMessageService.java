package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {

    private static final Path FILE_PATH = Paths.get("data/messages.ser");

    public FileMessageService() {}

    private Map<UUID, Message> loadData() {
        if (!Files.exists(FILE_PATH)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_PATH.toFile()))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Message> data) {
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
    public Message create(String content, UUID channelId, UUID authorId) {
        Map<UUID, Message> data = loadData();

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메세지 내용을 입력해주세요.");
        }
        if (channelId == null) {
            throw new IllegalArgumentException("채널 아이디를 입력해주세요.");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("작성자 아이디를 입력해주세요.");
        }

        Message message = new Message(content, channelId, authorId);
        data.put(message.getId(), message);
        saveData(data);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return loadData().get(id);
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return loadData().values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public boolean update(UUID id, String newContent) {
        Map<UUID, Message> data = loadData();

        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("메세지 내용을 입력해주세요.");
        }

        Message message = data.get(id);
        if (message == null) return false;

        message.update(newContent);
        saveData(data);
        return true;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> data = loadData();
        data.remove(id);
        saveData(data);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, Message> data = loadData();
        data.values().removeIf(m -> m.getChannelId().equals(channelId));
        saveData(data);
    }

    @Override
    public void deleteByAuthorId(UUID authorId) {
        Map<UUID, Message> data = loadData();
        data.values().removeIf(m -> m.getAuthorId().equals(authorId));
        saveData(data);
    }
}