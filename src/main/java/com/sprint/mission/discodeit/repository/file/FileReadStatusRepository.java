package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path filePath;

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory) {
        this.filePath = Paths.get(fileDirectory).resolve("read-statuses.ser");
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ReadStatus> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Map<UUID, ReadStatus>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[ReadStatusRepository] 클래스 구조 불일치로 역직렬화에 실패했습니다.", e);
        } catch (IOException e) {
            throw new RuntimeException("[ReadStatusRepository] 파일이 손상되었거나 읽을 수 없습니다: " + filePath, e);
        }
    }

    private void saveData(Map<UUID, ReadStatus> data) {
        try {
            Files.createDirectories(filePath.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Map<UUID, ReadStatus> data = loadData();
        data.put(readStatus.getId(), readStatus);
        saveData(data);
        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        return loadData().get(id);
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return loadData().values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId)
                        && readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return loadData().values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return loadData().values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, ReadStatus> data = loadData();
        data.remove(id);
        saveData(data);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> data = loadData();
        data.values().removeIf(readStatus -> readStatus.getChannelId().equals(channelId));
        saveData(data);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, ReadStatus> data = loadData();
        data.values().removeIf(readStatus -> readStatus.getUserId().equals(userId));
        saveData(data);
    }
}
