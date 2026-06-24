package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
@RestController
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @RequestMapping(value = "/api/binary-contents/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@PathVariable UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.findById(binaryContentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(binaryContent.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(binaryContent.getFileName())
                        .build()
                        .toString())
                .body(binaryContent.getBytes());
    }

    @RequestMapping(value = "/api/binary-contents", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(ids));
    }

    @RequestMapping(value = "/api/binaryContents", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllByIdInSpec(@RequestParam List<UUID> binaryContentIds) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }

    @RequestMapping(value = "/api/binaryContents/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> findSpec(@PathVariable UUID binaryContentId) {
        return ResponseEntity.ok(binaryContentService.findById(binaryContentId));
    }

    @RequestMapping(value = "/api/binaryContent/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponse> find(@RequestParam UUID binaryContentId) {
        return ResponseEntity.ok(BinaryContentResponse.from(binaryContentService.findById(binaryContentId)));
    }
}
