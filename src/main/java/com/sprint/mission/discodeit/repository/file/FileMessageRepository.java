package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileMessageRepository implements MessageRepository {
//    private static final Path filePath = Paths.get("data/messages.ser");
    private final Path filePath;
    public FileMessageRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory) {
        this.filePath = Paths.get(fileDirectory).resolve("messages.ser");
    }
    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadData() {
        if(!Files.exists(filePath)){
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath.toFile()))){
            return (Map<UUID, Message>) ois.readObject();
        }catch (ClassNotFoundException e){
            throw new RuntimeException("[MessageRepository] 클래스 구조 불일치로 역직렬화에 실패했습니다.", e);
        }catch (IOException e){
            throw new RuntimeException("[MessageRepository] 파일이 손상되었거나 읽을 수 없습니다: " + filePath, e);
        }
    }

    private void saveData(Map<UUID, Message> data){
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
        data.values().removeIf(message -> Objects.equals(message.getAuthorId(), authorId));
        saveData(data);
    }
}
