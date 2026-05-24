package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelService implements ChannelService {

    private final MessageService messageService;

    private static final Path FILE_PATH = Paths.get("data/channels.ser");

    public FileChannelService(MessageService messageService) {
        this.messageService = messageService;
    }

    private Map<UUID, Channel> loadData() {
        if (!Files.exists(FILE_PATH)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_PATH.toFile()))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Channel> data) {
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
    public Channel create(ChannelType type, String name, String description, UUID authorId) {
        Map<UUID, Channel> data = loadData();

        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("채널명을 입력해주세요.");
        }
        Channel channel = new Channel(type, name, description, authorId);
        data.put(channel.getId(), channel);
        saveData(data);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return loadData().get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public boolean update(UUID id, ChannelType newType, String newName, String newDescription) {
        Map<UUID, Channel> data = loadData();

        Channel channel = data.get(id);
        if (channel == null) {
            return false; // 채널 없으면 false 반환
        }
        channel.update(
                newType != null ? newType : channel.getType(),
                newName != null ? newName : channel.getName(),
                newDescription != null ? newDescription : channel.getDescription()
        );
        saveData(data);
        return true;
    }

    @Override
    public void delete(UUID id) {
        messageService.deleteByChannelId(id);
        Map<UUID, Channel> data = loadData();
        data.remove(id);
        saveData(data);
    }

    @Override
    public void deleteByAuthorId(UUID authorId) {
        Map<UUID, Channel> data = loadData();
        data.values().stream()
                .filter(c -> c.getAuthorId().equals(authorId))
                .forEach(c -> messageService.deleteByChannelId(c.getId()));
        data.values().removeIf(c -> c.getAuthorId().equals(authorId));
        saveData(data);

    }
}
