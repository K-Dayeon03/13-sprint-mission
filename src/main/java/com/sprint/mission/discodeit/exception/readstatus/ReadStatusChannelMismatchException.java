package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReadStatusChannelMismatchException extends ReadStatusException {
    public ReadStatusChannelMismatchException(UUID channelId, UUID readStatusId) {
        super(ErrorCode.READ_STATUS_CHANNEL_MISMATCH, Map.of(
                "channelId", channelId,
                "readStatusId", readStatusId
        ));
    }
}
