package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.global.GlobalExceptionHandler;
import com.sprint.mission.discodeit.mapper.ChannelCommandMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ChannelService channelService;

    @MockitoBean
    ChannelCommandMapper channelCommandMapper;

    @Test
    @DisplayName("PUBLIC 채널 생성 성공")
    void createPublic_success() throws Exception {
        UUID channelId = UUID.randomUUID();
        CreatePublicChannelRequest request = new CreatePublicChannelRequest("general", "general channel");
        CreatePublicChannelCommand command = new CreatePublicChannelCommand("general", "general channel");
        ChannelDto response = new ChannelDto(channelId, ChannelType.PUBLIC, "general", "general channel", null, null);

        given(channelCommandMapper.toCreatePublicCommand(any(CreatePublicChannelRequest.class))).willReturn(command);
        given(channelService.createPublic(command)).willReturn(response);

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("general"))
                .andExpect(jsonPath("$.description").value("general channel"));
    }

    @Test
    @DisplayName("PUBLIC 채널 생성 실패 - 유효성 검증")
    void createPublic_fail_validation() throws Exception {
        CreatePublicChannelRequest request = new CreatePublicChannelRequest("", "general channel");

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.name").exists());
    }

    @Test
    @DisplayName("채널 수정 실패 - 존재하지 않는 채널")
    void update_fail_channelNotFound() throws Exception {
        UUID channelId = UUID.randomUUID();

        given(channelService.update(any(UUID.class), any())).willThrow(new ChannelNotFoundException(channelId));

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"newName":"new-general","newDescription":"new description"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("ChannelNotFoundException"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
