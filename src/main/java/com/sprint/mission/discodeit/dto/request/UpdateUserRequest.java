package com.sprint.mission.discodeit.dto.request;
//들어오는 데이터
public record UpdateUserRequest(
   String newUsername,
   String newEmail,
   String newPassword
) {}
