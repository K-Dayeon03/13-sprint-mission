package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private static final Path FILE_PATH = Paths.get("data/channels.ser");
    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadData() {
        if(!Files.exists(FILE_PATH)){
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_PATH.toFile()))){
            return (Map<UUID, Channel>) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Channel> data){
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try(ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(FILE_PATH.toFile()))){
                oos.writeObject(data);
            }

        }catch (IOException e){
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }
    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> data = loadData();
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
    public void deleteById(UUID id) {
        Map<UUID, Channel> data = loadData();
        data.remove(id);
        saveData(data);
    }

    @Override
    public void deleteByAuthorId(UUID authorId) {
        Map<UUID, Channel> data = loadData();
        data.values().removeIf(m -> m.getAuthorId().equals(authorId));
        saveData(data);
    }
}
