package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.mapper.ChannelCommandMapper;
import com.sprint.mission.discodeit.service.ChannelService;
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

@Tag(name = "Channel")
@Slf4j
@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;
    private final ChannelCommandMapper channelCommandMapper;

    public ChannelController(ChannelService channelService, ChannelCommandMapper channelCommandMapper) {
        this.channelService = channelService;
        this.channelCommandMapper = channelCommandMapper;
    }

    @Operation(summary = "Public Channel 생성")
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPublic(@RequestBody CreatePublicChannelRequest request) {
        ChannelDto channel = channelService.createPublic(channelCommandMapper.toCreatePublicCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @Operation(summary = "Private Channel 생성")
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPrivate(@RequestBody CreatePrivateChannelRequest request) {
        ChannelDto channel = channelService.createPrivate(channelCommandMapper.toCreatePrivateCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

    @Operation(summary = "Channel 정보 수정")
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto> update(@PathVariable UUID channelId,
                                             @RequestBody UpdateChannelRequest request) {
        return ResponseEntity.ok(channelService.update(channelId, channelCommandMapper.toUpdateCommand(request)));
    }

    @Operation(summary = "Channel 삭제")
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.deleteById(channelId);
        return ResponseEntity.noContent().build();
    }
}
