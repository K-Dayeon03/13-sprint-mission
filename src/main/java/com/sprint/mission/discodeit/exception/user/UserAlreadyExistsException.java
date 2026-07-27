package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(String username) {
        super(ErrorCode.USER_ALREADY_EXISTS, Map.of("username", username));
    }

    public static UserAlreadyExistsException byEmail(String email) {
        return new UserAlreadyExistsException("email", email);
    }

    private UserAlreadyExistsException(String key, String value) {
        super(ErrorCode.USER_ALREADY_EXISTS, Map.of(key, value));
    }
}
