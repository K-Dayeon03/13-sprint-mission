package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

//들어오는 데이터
public record UpdateUserRequest(
   @Size(max = 50, message = "사용자 이름은 50자 이하여야 합니다.")
   String newUsername,

   @Email(message = "이메일 형식이 올바르지 않습니다.")
   @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
   String newEmail,

   @Size(max = 60, message = "비밀번호는 60자 이하여야 합니다.")
   String newPassword
) {}
