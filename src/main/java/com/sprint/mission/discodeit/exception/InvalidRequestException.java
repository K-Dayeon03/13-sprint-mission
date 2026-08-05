package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class InvalidRequestException extends DiscodeitException {
    public InvalidRequestException(String reason) {
        super(ErrorCode.INVALID_REQUEST, Map.of("reason", reason));
    }

    public InvalidRequestException(Map<String, Object> details) {
        super(ErrorCode.INVALID_REQUEST, details);
    }

    public InvalidRequestException(String reason, Throwable cause) {
        super(ErrorCode.INVALID_REQUEST, Map.of("reason", reason), cause);
    }
}
