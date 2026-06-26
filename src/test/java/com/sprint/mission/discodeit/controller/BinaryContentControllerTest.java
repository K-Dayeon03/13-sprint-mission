package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class BinaryContentControllerTest {

    @Test
    @DisplayName("한글 파일명 다운로드 헤더를 UTF-8로 인코딩한다")
    void download_encodesKoreanFileName() {
        BinaryContentService binaryContentService = mock(BinaryContentService.class);
        BinaryContentController controller = new BinaryContentController(binaryContentService);
        BinaryContent binaryContent = new BinaryContent(
                null,
                UUID.randomUUID(),
                "요청서.png",
                "image/png",
                new byte[]{1, 2, 3}
        );
        given(binaryContentService.findById(binaryContent.getId())).willReturn(binaryContent);

        ResponseEntity<byte[]> response = controller.download(binaryContent.getId());

        String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertThat(contentDisposition)
                .contains("filename*=")
                .doesNotContain("요청서");
    }
}
