package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path filePath;
증
    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory) {
        this.filePath = Paths.get(fileDirectory).resolve("binary-contents.ser");
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[BinaryContentRepository] 클래스 구조 불일치로 역직렬화에 실패했습니다.", e);
        } catch (IOException e) {
            throw new RuntimeException("[BinaryContentRepository] 파일이 손상되었거나 읽을 수 없습니다: " + filePath, e);
        }
    }

    private void saveData(Map<UUID, BinaryContent> data) {
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
    public BinaryContent save(BinaryContent binaryContent) {
        Map<UUID, BinaryContent> data = loadData();
        data.put(binaryContent.getId(), binaryContent);
        saveData(data);
        return binaryContent;
    }

    @Override
    public BinaryContent findById(UUID id) {
        return loadData().get(id);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return loadData().values().stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, BinaryContent> data = loadData();
        data.remove(id);
        saveData(data);
    }

    @Override
    public void deleteAllByMessageId(UUID messageId) {
        Map<UUID, BinaryContent> data = loadData();
        data.values().removeIf(binaryContent -> messageId.equals(binaryContent.getMessageId()));
        saveData(data);
    }
}
