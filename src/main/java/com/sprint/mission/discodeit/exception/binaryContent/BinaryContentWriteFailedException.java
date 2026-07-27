package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class BinaryContentWriteFailedException extends BinaryContentException {
    public BinaryContentWriteFailedException(UUID binaryContentId, Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_WRITE_FAILED, Map.of("binaryContentId", binaryContentId), cause);
    }
}
