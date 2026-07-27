package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;
@Getter
public class DiscodeitException extends RuntimeException {
    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;
    public DiscodeitException(ErrorCode errorCode) {
        this(errorCode, Map.of());
        //details 예외가 발생한 추가 정보를 넣기
        //Map.of("userId", userId)
        //Map.of("channelId", channelId)
    }
    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }
}
