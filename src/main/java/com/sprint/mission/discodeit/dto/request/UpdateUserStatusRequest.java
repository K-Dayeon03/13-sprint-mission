package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

public record UpdateUserStatusRequest(
        Instant lastActiveAt
) {
}
