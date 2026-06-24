package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        UserResponse user = userService.create(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> createWithProfileImage(
            @RequestPart(value = "userCreateRequest", required = false) CreateUserRequest userCreateRequest,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        CreateUserRequest request = userCreateRequest != null
                ? userCreateRequest
                : new CreateUserRequest(username, email, password);
        UserResponse user = userService.create(request, toBinaryContentRequest(profile));
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findByAll());
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> update(@PathVariable UUID userId, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(userId, request, null));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateWithProfileImage(
            @PathVariable UUID userId,
            @RequestPart(value = "userUpdateRequest", required = false) UpdateUserRequest userUpdateRequest,
            @RequestParam(required = false) String newUsername,
            @RequestParam(required = false) String newEmail,
            @RequestParam(required = false) String newPassword,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UpdateUserRequest request = userUpdateRequest != null
                ? userUpdateRequest
                : new UpdateUserRequest(newUsername, newEmail, newPassword);
        return ResponseEntity.ok(userService.update(userId, request, toBinaryContentRequest(profile)));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = {"/{userId}/status", "/{userId}/userStatus"}, method = RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateStatus(@PathVariable UUID userId,
                                                   @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(userStatusService.updateByUserId(userId, request));
    }

    private CreateBinaryContentRequest toBinaryContentRequest(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.isBlank()) {
                fileName = "profile-image";
            }

            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return new CreateBinaryContentRequest(fileName, contentType, file.getBytes());
        } catch (IOException e) {
            throw new BadRequestException("프로필 이미지를 읽을 수 없습니다.", e);
        }
    }
}
