package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService{
        User create(String username, String password, String email);
        User findById(UUID id);
        List<User> findByAll();
        User update(UUID id, String currentPassword, String newUsername, String newPassword, String newEmail);
        void deleteById(UUID id);
    }