package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent")
@RestController
public class BinaryContentController {
    //기존에는 DTO나 엔티티에서 직접 bytes를 꺼냈다면, 이제 Storage에 위임합니다.
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;
    public BinaryContentController(BinaryContentStorage binaryContentStorage, BinaryContentService binaryContentService) {
        this.binaryContentStorage = binaryContentStorage;
        this.binaryContentService = binaryContentService;
    }


    @Operation(summary = "파일 다운로드")
    @RequestMapping(
            value = {"/api/binary-contents/{binaryContentId}", "/api/binaryContents/{binaryContentId}/download"},
            method = RequestMethod.GET
    )
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
        BinaryContentDto binaryContent = binaryContentService.findById(binaryContentId);
        return binaryContentStorage.download(binaryContent);
    }

    @Operation(summary = "여러 첨부 파일 조회")
    @RequestMapping(value = "/api/binary-contents", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(ids));
    }

    @Operation(summary = "여러 첨부 파일 조회")
    @RequestMapping(value = "/api/binaryContents", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto>> findAllByIdInSpec(@RequestParam List<UUID> binaryContentIds) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }

    @Operation(summary = "첨부 파일 조회")
    @RequestMapping(value = "/api/binaryContents/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentDto> findSpec(@PathVariable UUID binaryContentId) {
        return ResponseEntity.ok(binaryContentService.findById(binaryContentId));
    }

    @Operation(summary = "첨부 파일 조회")
    @RequestMapping(value = "/api/binaryContent/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentDto> find(@RequestParam UUID binaryContentId) {
        return ResponseEntity.ok(binaryContentService.findById(binaryContentId));
    }
}
