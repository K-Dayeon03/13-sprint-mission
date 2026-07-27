package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class BinaryContentStorageInitFailedException extends BinaryContentException {
    public BinaryContentStorageInitFailedException(String rootPath, Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_STORAGE_INIT_FAILED, Map.of("rootPath", rootPath), cause);
    }
}
