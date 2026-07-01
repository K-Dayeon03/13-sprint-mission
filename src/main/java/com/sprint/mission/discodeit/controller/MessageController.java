package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
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
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(value = "/api/messages", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> create(@RequestBody CreateMessageRequest request) {
        Message message = messageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.from(message));
    }

    @RequestMapping(value = "/api/messages", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createWithAttachments(
            @RequestPart("messageCreateRequest") CreateMessageRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        CreateMessageRequest request = new CreateMessageRequest(
                messageCreateRequest.content(),
                messageCreateRequest.channelId(),
                messageCreateRequest.authorId(),
                toBinaryContentRequests(attachments)
        );
        Message message = messageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.from(message));
    }

    @RequestMapping(value = "/api/messages/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponse> update(@PathVariable UUID messageId, @RequestBody UpdateMessageRequest request) {
        return ResponseEntity.ok(MessageResponse.from(messageService.update(messageId, request)));
    }

    @RequestMapping(value = "/api/messages/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.deleteById(messageId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/api/channels/{channelId}/messages", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> findAllByChannelId(@PathVariable UUID channelId) {
        return findAllByChannelIdQuery(channelId);
    }

    @RequestMapping(value = "/api/messages", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> findAllByChannelIdQuery(@RequestParam UUID channelId) {
        List<MessageResponse> messages = messageService.findAllByChannelId(channelId).stream()
                .map(MessageResponse::from)
                .toList();
        return ResponseEntity.ok(messages);
    }

    private List<CreateBinaryContentRequest> toBinaryContentRequests(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(this::toBinaryContentRequest)
                .toList();
    }

    private CreateBinaryContentRequest toBinaryContentRequest(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.isBlank()) {
                fileName = "attachment";
            }
            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            return new CreateBinaryContentRequest(fileName, contentType, file.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("첨부파일을 읽을 수 없습니다.", e);
        }
    }
}
