package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<CreateBinaryContentRequest> attachments ///선택적 첨부파일 목록
) {
}
