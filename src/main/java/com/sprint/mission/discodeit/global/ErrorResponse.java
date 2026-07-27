package com.sprint.mission.discodeit.global;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType,
        int status
) {
    public static ErrorResponse from(DiscodeitException e) {
        ErrorCode errorCode = e.getErrorCode();

        return new ErrorResponse(
                e.getTimestamp(),
                errorCode.name(),
                errorCode.getMessage(),
                e.getDetails(),
                e.getClass().getSimpleName(),
                errorCode.getStatus().value()
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, Exception e, Map<String, Object> details) {
        return new ErrorResponse(
                Instant.now(),
                errorCode.name(),
                errorCode.getMessage(),
                details,
                e.getClass().getSimpleName(),
                errorCode.getStatus().value()
        );
    }

    public static ErrorResponse of(HttpStatus status, String code, String message, Exception e) {
        return new ErrorResponse(
                Instant.now(),
                code,
                message,
                Map.of(),
                e.getClass().getSimpleName(),
                status.value()
        );
    }
}