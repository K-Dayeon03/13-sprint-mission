package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.global.GlobalExceptionHandler;
import com.sprint.mission.discodeit.mapper.MessageCommandMapper;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MessageService messageService;

    @MockitoBean
    MessageCommandMapper messageCommandMapper;

    @Test
    @DisplayName("메시지 생성 성공")
    void create_success() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        CreateMessageRequest request = new CreateMessageRequest("hello", channelId, authorId, List.of());
        CreateMessageCommand command = new CreateMessageCommand("hello", channelId, authorId, List.of());
        UserDto author = new UserDto(authorId, "woody", "woody@codeit.com", null, true);
        MessageDto response = new MessageDto(
                messageId,
                Instant.parse("2026-07-27T09:00:00Z"),
                null,
                "hello",
                channelId,
                author,
                List.of()
        );

        given(messageCommandMapper.toCreateCommand(any(CreateMessageRequest.class))).willReturn(command);
        given(messageService.create(command)).willReturn(response);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("hello"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.author.id").value(authorId.toString()));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 유효성 검증")
    void create_fail_validation() throws Exception {
        CreateMessageRequest request = new CreateMessageRequest("", null, null, List.of());

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.content").exists())
                .andExpect(jsonPath("$.details.channelId").exists())
                .andExpect(jsonPath("$.details.authorId").exists());
    }

    @Test
    @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
    void update_fail_messageNotFound() throws Exception {
        UUID messageId = UUID.randomUUID();

        given(messageService.update(any(UUID.class), any())).willThrow(new MessageNotFoundException(messageId));

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"newContent":"updated"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("MessageNotFoundException"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
