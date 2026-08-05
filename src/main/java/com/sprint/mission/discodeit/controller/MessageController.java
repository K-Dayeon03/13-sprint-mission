package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.mapper.MessageCommandMapper;
import com.sprint.mission.discodeit.service.MessageService;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message")
@Slf4j
@RestController
public class MessageController {
    private final MessageService messageService;
    private final MessageCommandMapper messageCommandMapper;

    public MessageController(MessageService messageService, MessageCommandMapper messageCommandMapper) {
        this.messageService = messageService;
        this.messageCommandMapper = messageCommandMapper;
    }

    @Operation(summary = "Message 생성")
    @RequestMapping(value = "/api/messages", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDto> create(@Valid @RequestBody CreateMessageRequest request) {
        MessageDto message = messageService.create(messageCommandMapper.toCreateCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @Operation(summary = "Message 생성")
    @RequestMapping(value = "/api/messages", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> createWithAttachments(
            @Valid @RequestPart("messageCreateRequest") CreateMessageRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        MessageDto message = messageService.create(messageCommandMapper.toCreateCommand(messageCreateRequest, attachments));
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @Operation(summary = "Message 내용 수정")
    @RequestMapping(value = "/api/messages/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageDto> update(@PathVariable UUID messageId, @Valid @RequestBody UpdateMessageRequest request) {
        return ResponseEntity.ok(messageService.update(messageId, messageCommandMapper.toUpdateCommand(request)));
    }

    @Operation(summary = "Message 삭제")
    @RequestMapping(value = "/api/messages/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.deleteById(messageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Channel의 Message 목록 조회")
    @RequestMapping(value = "/api/channels/{channelId}/messages", method = RequestMethod.GET)
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
            @PathVariable UUID channelId,
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId, cursor, size));
    }

    @Operation(summary = "Channel의 Message 목록 조회")
    @RequestMapping(value = "/api/messages", method = RequestMethod.GET)
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelIdQuery(
            @RequestParam UUID channelId,
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId, cursor, size));
    }
}
