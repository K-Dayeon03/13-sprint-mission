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
    //하드코딩 경로 제거 후 생성자 주입으로 교체
    // private static final Path filePath = Paths.get("data/users.ser");
    private final Path filePath;
    public FileUserRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory) {
        this.filePath = Paths.get(fileDirectory).resolve("users.ser");
    }
    @SuppressWarnings("unchecked")//경고 없애기
    private Map<UUID, User> loadData() {
        if (!Files.exists(filePath)) {
            // 파일이 없는 건 정상 — 조용히 빈 Map 반환
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath.toFile()))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            // 파일이 없는 경우 (exists 체크 후 삭제된 극히 드문 경우)
            System.err.println("[UserRepository] 파일을 찾을 수 없습니다: " + e.getMessage());
            return new HashMap<>();
        } catch (ClassNotFoundException e) {
            // 클래스 구조가 바뀌어 역직렬화 실패 — 심각한 문제
            throw new RuntimeException("[UserRepository] 클래스 구조 불일치로 역직렬화에 실패했습니다.", e);
        } catch (IOException e) {
            // 파일이 깨진 경우 — 조용히 넘기면 데이터 유실을 모를 수 있음
            throw new RuntimeException("[UserRepository] 파일이 손상되었습니다: " + filePath, e);
        }
    }

    private void saveData(Map<UUID, User> data){
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

    @Override
    public void deleteById(UUID id) {
        Map<UUID, User> data = loadData();
        data.remove(id);
        saveData(data);
    }

}
