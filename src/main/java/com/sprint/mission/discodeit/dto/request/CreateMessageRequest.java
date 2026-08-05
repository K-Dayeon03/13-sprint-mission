package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequest(
        @NotBlank(message = "메시지 내용은 필수입니다.")
        String content,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId,

        @NotNull(message = "작성자 ID는 필수입니다.")
        UUID authorId,

        List<@Valid CreateBinaryContentRequest> attachments ///선택적 첨부파일 목록
) {
}
