package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

public record CreatePrivateChannelRequest(
        List<UUID> participantIds //참여할 유저 아이디 목록
) {
}
