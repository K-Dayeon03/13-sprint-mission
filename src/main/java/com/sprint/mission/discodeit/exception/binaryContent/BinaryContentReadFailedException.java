package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class BinaryContentReadFailedException extends BinaryContentException {
    public BinaryContentReadFailedException(UUID binaryContentId, Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_READ_FAILED, Map.of("binaryContentId", binaryContentId), cause);
    }
}
