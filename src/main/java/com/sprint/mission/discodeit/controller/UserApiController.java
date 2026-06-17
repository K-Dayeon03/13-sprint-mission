package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
//profileImageId()를 요구사항의 profileId로 매핑
@RestController
@RequestMapping("/api/user")
public class UserApiController {
    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findByAll().stream()
                .map(user -> new UserDto(
                        user.id(),
                        user.createdAt(),
                        user.updatedAt(),
                        user.username(),
                        user.email(),
                        user.profileImageId(),
                        user.isOnline()
                ))
                .toList();

        return ResponseEntity.ok(users);
    }
}