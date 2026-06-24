package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    @RequestMapping(value = "/api/read-statuses", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(@RequestBody CreateReadStatusRequest request) {
        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @RequestMapping(value = "/api/channels/{channelId}/read-statuses", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> createByChannelId(@PathVariable UUID channelId,
                                                        @RequestBody CreateChannelReadStatusRequest request) {
        ReadStatus readStatus = readStatusService.create(new CreateReadStatusRequest(request.userId(), channelId));
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @RequestMapping(value = "/api/read-statuses/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatus> update(@PathVariable UUID readStatusId,
                                             @RequestBody UpdateReadStatusRequest request) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    @RequestMapping(value = "/api/channels/{channelId}/read-statuses/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatus> updateByChannelId(@PathVariable UUID channelId,
                                                        @PathVariable UUID readStatusId,
                                                        @RequestBody UpdateReadStatusRequest request) {
        ReadStatus readStatus = readStatusService.findById(readStatusId);
        if (!channelId.equals(readStatus.getChannelId())) {
            throw new IllegalArgumentException("채널에 해당하는 ReadStatus가 아닙니다.");
        }
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    @RequestMapping(value = "/api/users/{userId}/read-statuses", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    public record CreateChannelReadStatusRequest(UUID userId) {
    }
}
