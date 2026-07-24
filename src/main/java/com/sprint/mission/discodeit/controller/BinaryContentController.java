package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent")
@RestController
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @Operation(summary = "파일 다운로드")
    @RequestMapping(value = {"/api/binary-contents/{binaryContentId}", "/api/binaryContents/{binaryContentId}/download"}, method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@PathVariable UUID binaryContentId) {
        BinaryContentDto binaryContent = binaryContentService.findById(binaryContentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(binaryContent.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(binaryContent.fileName(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(binaryContent.bytes());
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
