package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.mapper.BinaryContentCommandMapper;
import com.sprint.mission.discodeit.mapper.UserCommandMapper;
import com.sprint.mission.discodeit.mapper.UserStatusCommandMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

@Tag(name = "User")
@Slf4j
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

    @Operation(summary = "User 등록")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.create(userCommandMapper.toCreateCommand(request), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "User 등록")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> createWithProfileImage(
            @Valid @RequestPart(value = "userCreateRequest", required = false) CreateUserRequest userCreateRequest,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        log.debug("Received user create request. username={}, email={}, hasProfileImage={}",
                userCreateRequest != null ? userCreateRequest.username() : username,
                userCreateRequest != null ? userCreateRequest.email() : email,
                profile != null && !profile.isEmpty());

        UserDto user = userService.create(
                userCreateRequest != null
                        ? userCommandMapper.toCreateCommand(userCreateRequest)
                        : userCommandMapper.toCreateCommand(username, email, password),
                binaryContentCommandMapper.toCommand(profile, "profile-image")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "전체 User 목록 조회")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findByAll());
    }

    @Operation(summary = "User 정보 수정")
    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> update(@PathVariable UUID userId, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(userId, userCommandMapper.toUpdateCommand(request), null));
    }

    @Operation(summary = "User 정보 수정")
    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateWithProfileImage(
            @PathVariable UUID userId,
            @Valid @RequestPart(value = "userUpdateRequest", required = false) UpdateUserRequest userUpdateRequest,
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

    @Operation(summary = "User 삭제")
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "User 온라인 상태 업데이트")
    @RequestMapping(value = {"/{userId}/status", "/{userId}/userStatus"}, method = RequestMethod.PATCH)
    public ResponseEntity<UserStatusDto> updateStatus(@PathVariable UUID userId,
                                                      @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(userStatusService.updateByUserId(userId, userStatusCommandMapper.toUpdateCommand(request)));
    }
}
