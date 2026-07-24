package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BinaryContentControllerTest {

    @Test
    @DisplayName("파일 다운로드는 BinaryContentStorage에 위임한다")
    void download_delegatesToStorage() {
        BinaryContentStorage binaryContentStorage = mock(BinaryContentStorage.class);
        BinaryContentService binaryContentService = mock(BinaryContentService.class);
        BinaryContentController controller = new BinaryContentController(binaryContentStorage, binaryContentService);

        UUID binaryContentId = UUID.randomUUID();
        BinaryContentDto dto = new BinaryContentDto(
                binaryContentId,
                Instant.now(),
                "요청서.png",
                "image/png",
                3L,
                "/api/binaryContents/" + binaryContentId + "/download"
        );
        ResponseEntity<?> expected = ResponseEntity.ok(new ByteArrayResource(new byte[]{1, 2, 3}));

        given(binaryContentService.findById(binaryContentId)).willReturn(dto);
        doReturn(expected).when(binaryContentStorage).download(dto);

        ResponseEntity<?> result = controller.download(binaryContentId);

        assertThat(result).isSameAs(expected);
        verify(binaryContentService).findById(binaryContentId);
        verify(binaryContentStorage).download(dto);
    }
}
