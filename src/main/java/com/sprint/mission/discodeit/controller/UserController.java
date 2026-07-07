package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentCommandMapper;
import com.sprint.mission.discodeit.mapper.UserCommandMapper;
import com.sprint.mission.discodeit.mapper.UserStatusCommandMapper;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final UserCommandMapper userCommandMapper;
    private final UserStatusCommandMapper userStatusCommandMapper;
    private final BinaryContentCommandMapper binaryContentCommandMapper;

    public UserController(UserService userService,
                          UserStatusService userStatusService,
                          UserCommandMapper userCommandMapper,
                          UserStatusCommandMapper userStatusCommandMapper,
                          BinaryContentCommandMapper binaryContentCommandMapper) {
        this.userService = userService;
        this.userStatusService = userStatusService;
        this.userCommandMapper = userCommandMapper;
        this.userStatusCommandMapper = userStatusCommandMapper;
        this.binaryContentCommandMapper = binaryContentCommandMapper;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        UserResponse user = userService.create(userCommandMapper.toCreateCommand(request), null);
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
        UserResponse user = userService.create(
                userCreateRequest != null
                        ? userCommandMapper.toCreateCommand(userCreateRequest)
                        : userCommandMapper.toCreateCommand(username, email, password),
                binaryContentCommandMapper.toCommand(profile, "profile-image")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findByAll());
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> update(@PathVariable UUID userId, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(userId, userCommandMapper.toUpdateCommand(request), null));
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
        return ResponseEntity.ok(userService.update(
                userId,
                userUpdateRequest != null
                        ? userCommandMapper.toUpdateCommand(userUpdateRequest)
                        : userCommandMapper.toUpdateCommand(newUsername, newEmail, newPassword),
                binaryContentCommandMapper.toCommand(profile, "profile-image")
        ));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = {"/{userId}/status", "/{userId}/userStatus"}, method = RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateStatus(@PathVariable UUID userId,
                                                   @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(userStatusService.updateByUserId(userId, userStatusCommandMapper.toUpdateCommand(request)));
    }
}
