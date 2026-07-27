package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreatePrivateChannelRequest(
        @NotEmpty(message = "PRIVATE 채널 참여자는 1명 이상이어야 합니다.")
        List<@NotNull(message = "참여자 ID는 필수입니다.") UUID> participantIds //참여할 유저 아이디 목록
) {
}
