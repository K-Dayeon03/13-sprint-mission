package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.command.CreateUserCommand;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.global.GlobalExceptionHandler;
import com.sprint.mission.discodeit.mapper.BinaryContentCommandMapper;
import com.sprint.mission.discodeit.mapper.UserCommandMapper;
import com.sprint.mission.discodeit.mapper.UserStatusCommandMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @MockitoBean
    UserStatusService userStatusService;

    @MockitoBean
    UserCommandMapper userCommandMapper;

    @MockitoBean
    UserStatusCommandMapper userStatusCommandMapper;

    @MockitoBean
    BinaryContentCommandMapper binaryContentCommandMapper;

    @Test
    @DisplayName("사용자 생성 성공")
    void create_success() throws Exception {
        UUID userId = UUID.randomUUID();
        CreateUserRequest request = new CreateUserRequest("woody", "woody@codeit.com", "Password1!");
        CreateUserCommand command = new CreateUserCommand("woody", "woody@codeit.com", "Password1!");
        UserResponse response = new UserResponse(userId, "woody", "woody@codeit.com", null, true);

        given(userCommandMapper.toCreateCommand(any(CreateUserRequest.class))).willReturn(command);
        given(userService.create(command, null)).willReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("woody"))
                .andExpect(jsonPath("$.email").value("woody@codeit.com"))
                .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    @DisplayName("사용자 생성 실패 - 유효성 검증")
    void create_fail_validation() throws Exception {
        CreateUserRequest request = new CreateUserRequest("", "invalid-email", "");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.username").exists())
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.password").exists());
    }

    @Test
    @DisplayName("사용자 생성 실패 - multipart DTO part 누락")
    void createWithProfileImage_fail_missingRequestPart() throws Exception {
        mockMvc.perform(multipart("/api/users")
                        .param("username", "woody")
                        .param("email", "woody@codeit.com")
                        .param("password", "Password1!"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verify(userService, never()).create(any(), any());
    }

    @Test
    @DisplayName("사용자 생성 실패 - multipart DTO 유효성 검증")
    void createWithProfileImage_fail_validation() throws Exception {
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                """
                {"username":"","email":"invalid-email","password":"short"}
                """.getBytes()
        );

        mockMvc.perform(multipart("/api/users").file(requestPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details.username").exists())
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.password").exists());

        verify(userService, never()).create(any(), any());
    }

    @Test
    @DisplayName("사용자 생성 실패 - 중복 사용자")
    void create_fail_duplicateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("woody", "woody@codeit.com", "Password1!");
        CreateUserCommand command = new CreateUserCommand("woody", "woody@codeit.com", "Password1!");

        given(userCommandMapper.toCreateCommand(any(CreateUserRequest.class))).willReturn(command);
        given(userService.create(any(CreateUserCommand.class), isNull())).willThrow(new UserAlreadyExistsException("woody"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.exceptionType").value("UserAlreadyExistsException"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
