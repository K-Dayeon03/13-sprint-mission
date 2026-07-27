package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 사용자 이름 또는 이메일입니다."),
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 UserStatus입니다."),
    USER_STATUS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 UserStatus입니다."),

    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채널입니다."),
    PRIVATE_CHANNEL_UPDATE(HttpStatus.BAD_REQUEST, "PRIVATE 채널은 수정할 수 없습니다."),

    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메시지입니다."),

    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다."),
    BINARY_CONTENT_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "파일 데이터를 찾을 수 없습니다."),
    BINARY_CONTENT_READ_FAILED(HttpStatus.BAD_REQUEST, "파일을 읽을 수 없습니다."),
    BINARY_CONTENT_WRITE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
    BINARY_CONTENT_STORAGE_INIT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장소 초기화에 실패했습니다."),

    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 ReadStatus입니다."),
    READ_STATUS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 ReadStatus입니다."),
    READ_STATUS_CHANNEL_MISMATCH(HttpStatus.BAD_REQUEST, "채널에 해당하는 ReadStatus가 아닙니다."),
    AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "유저 이름 또는 비밀번호가 일치하지 않습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "요청 데이터 검증에 실패했습니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 Content-Type입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "업로드 가능한 파일 크기를 초과했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
