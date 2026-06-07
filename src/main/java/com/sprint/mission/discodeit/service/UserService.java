package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService{
        UserResponse create(CreateUserRequest userRequest, CreateBinaryContentRequest profileImageRequest);
        UserResponse findById(UUID id);
        List<UserResponse> findByAll();
        UserResponse update(UUID id, UpdateUserRequest userRequest, CreateBinaryContentRequest profileImageRequest);
        void deleteById(UUID id);
    }