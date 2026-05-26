package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private static final Path FILE_PATH = Paths.get("data/users.ser");
    @SuppressWarnings("unchecked")//경고 없애기
    private Map<UUID, User> loadData() {
        if(!Files.exists(FILE_PATH)){
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(FILE_PATH.toFile()))){
            return (Map<UUID, User>) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, User> data){
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
    public List<User> findAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, User> data = loadData();
        data.remove(id);
        saveData(data);
    }
}
