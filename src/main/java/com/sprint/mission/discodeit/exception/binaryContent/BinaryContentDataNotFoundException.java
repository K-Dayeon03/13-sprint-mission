package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class BinaryContentDataNotFoundException extends BinaryContentException {
    public BinaryContentDataNotFoundException(UUID binaryContentId) {
        super(ErrorCode.BINARY_CONTENT_DATA_NOT_FOUND, Map.of("binaryContentId", binaryContentId));
    }
}
