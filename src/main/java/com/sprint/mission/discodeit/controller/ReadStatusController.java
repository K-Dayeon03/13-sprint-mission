package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.mapper.ReadStatusCommandMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus")
@Slf4j
@RestController
public class ReadStatusController {
    private final ReadStatusService readStatusService;
    private final ReadStatusCommandMapper readStatusCommandMapper;

    public ReadStatusController(ReadStatusService readStatusService, ReadStatusCommandMapper readStatusCommandMapper) {
        this.readStatusService = readStatusService;
        this.readStatusCommandMapper = readStatusCommandMapper;
    }

    @Operation(summary = "Message 읽음 상태 생성")
    @RequestMapping(value = {"/api/read-statuses", "/api/readStatuses"}, method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto> create(@RequestBody CreateReadStatusRequest request) {
        ReadStatusDto readStatus = readStatusService.create(readStatusCommandMapper.toCreateCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @Operation(summary = "Message 읽음 상태 생성")
    @RequestMapping(value = "/api/channels/{channelId}/read-statuses", method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto> createByChannelId(@PathVariable UUID channelId,
                                                           @RequestBody CreateChannelReadStatusRequest request) {
        ReadStatusDto readStatus = readStatusService.create(readStatusCommandMapper.toCreateCommand(request.userId(), channelId));
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @Operation(summary = "Message 읽음 상태 수정")
    @RequestMapping(value = {"/api/read-statuses/{readStatusId}", "/api/readStatuses/{readStatusId}"}, method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusDto> update(@PathVariable UUID readStatusId,
                                                @RequestBody UpdateReadStatusRequest request) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, readStatusCommandMapper.toUpdateCommand(request)));
    }

    @Operation(summary = "Message 읽음 상태 수정")
    @RequestMapping(value = "/api/channels/{channelId}/read-statuses/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusDto> updateByChannelId(@PathVariable UUID channelId,
                                                           @PathVariable UUID readStatusId,
                                                           @RequestBody UpdateReadStatusRequest request) {
        ReadStatusDto readStatus = readStatusService.findById(readStatusId);
        if (!channelId.equals(readStatus.channelId())) {
            throw new BadRequestException("채널에 해당하는 ReadStatus가 아닙니다.");
        }
        return ResponseEntity.ok(readStatusService.update(readStatusId, readStatusCommandMapper.toUpdateCommand(request)));
    }

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @RequestMapping(value = "/api/users/{userId}/read-statuses", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@PathVariable UUID userId) {
        return findAllByUserIdQuery(userId);
    }

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @RequestMapping(value = "/api/readStatuses", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto>> findAllByUserIdQuery(@RequestParam UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    public record CreateChannelReadStatusRequest(UUID userId) {
    }
}
