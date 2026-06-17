package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
public class FileUserRepository implements UserRepository {

    private final Path filePath;

    public FileUserRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory) {
        this.filePath = Paths.get(fileDirectory).resolve("users.ser");
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath.toFile()))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[UserRepository] 클래스 구조 불일치로 역직렬화에 실패했습니다.", e);
        } catch (IOException e) {
            throw new RuntimeException("[UserRepository] 파일이 손상되었거나 읽을 수 없습니다: " + filePath, e);
        }
    }

    private void saveData(Map<UUID, User> data){
        try {
            Files.createDirectories(filePath.getParent());
            try(ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(filePath.toFile()))){
                oos.writeObject(data);
            }
        } catch (IOException e){
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    @Override
    public User save(User user) {
        Map<UUID, User> data = loadData();
        data.put(user.getId(), user);
        saveData(data);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return loadData().get(id);
    }

    @Override
    public List<User> findByAll() {
        return new ArrayList<>(loadData().values());
    }

    // 💡 이 부분이 구현되었습니다.
    @Override
    public Optional<User> findByUsername(String username) {
        Map<UUID, User> data = loadData();
        return data.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, User> data = loadData();
        data.remove(id);
        saveData(data);
    }
}
