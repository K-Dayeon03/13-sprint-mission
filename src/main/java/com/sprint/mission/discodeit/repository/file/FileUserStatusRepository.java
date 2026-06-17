package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
import java.util.ArrayList;
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
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path filePath;

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory) {
        this.filePath = Paths.get(fileDirectory).resolve("user-statuses.ser");
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, UserStatus> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[UserStatusRepository] 클래스 구조 불일치로 역직렬화에 실패했습니다.", e);
        } catch (IOException e) {
            throw new RuntimeException("[UserStatusRepository] 파일이 손상되었거나 읽을 수 없습니다: " + filePath, e);
        }
    }

    private void saveData(Map<UUID, UserStatus> data) {
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
    public UserStatus save(UserStatus userStatus) {
        Map<UUID, UserStatus> data = loadData();
        data.put(userStatus.getUserId(), userStatus);
        saveData(data);
        return userStatus;
    }

    @Override
    public UserStatus findById(UUID id) {
        return loadData().values().stream()
                .filter(userStatus -> userStatus.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return Optional.ofNullable(loadData().get(userId));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, UserStatus> data = loadData();
        data.values().removeIf(userStatus -> userStatus.getId().equals(id));
        saveData(data);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, UserStatus> data = loadData();
        data.remove(userId);
        saveData(data);
    }
}
