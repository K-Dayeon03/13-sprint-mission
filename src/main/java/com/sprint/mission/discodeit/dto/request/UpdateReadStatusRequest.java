package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

public record UpdateReadStatusRequest(
        Instant lastReadAt
) {
}
