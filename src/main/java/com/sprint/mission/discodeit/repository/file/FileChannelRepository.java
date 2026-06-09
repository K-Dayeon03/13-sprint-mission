package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileChannelRepository implements ChannelRepository {
//    private static final Path filePath = Paths.get("data/channels.ser");
    private final Path filePath;
    public FileChannelRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory) {
        this.filePath = Paths.get(fileDirectory).resolve("channels.ser");
    }
    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadData() {
        if(!Files.exists(filePath)){
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath.toFile()))){
            return (Map<UUID, Channel>) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Channel> data){
        try {
            Files.createDirectories(filePath.getParent());
            try(ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(filePath.toFile()))){
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
    public List<Channel> findByAll() {
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
