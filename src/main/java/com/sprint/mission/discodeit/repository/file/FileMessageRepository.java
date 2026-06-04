package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
@Repository
public class FileMessageRepository implements MessageRepository {
    private static final Path FILE_PATH = Paths.get("data/messages.ser");
    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadData() {
        if(!Files.exists(FILE_PATH)){
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_PATH.toFile()))){
            return (Map<UUID, Message>) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Message> data){
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
    public Message save(Message message) {
        Map<UUID, Message> data = loadData();
        data.put(message.getId(), message);
        saveData(data);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return loadData().get(id);
    }

    @Override
    public List<Message> findByAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return loadData().values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
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
/*
모든 변경 메서드는 이 패턴!
Map<UUID, Message> data = loadData();  // 1. 파일에서 불러오기
data.변경작업();                        // 2. 변경
saveData(data);                        // 3. 파일에 저장
* */
    @Override
    public void deleteByAuthorId(UUID authorId) {
        Map<UUID, Message> data = loadData();
        data.values().removeIf(m -> m.getAuthorId().equals(authorId));
        saveData(data);
    }
}
