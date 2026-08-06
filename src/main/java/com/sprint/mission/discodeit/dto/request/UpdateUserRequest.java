package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//들어오는 데이터
public record UpdateUserRequest(
   @Size(max = 50, message = "사용자 이름은 50자 이하여야 합니다.")
   String newUsername,

   @Email(message = "이메일 형식이 올바르지 않습니다.")
   @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
   String newEmail,

   @Size(min = 8, max = 60, message = "비밀번호는 8자 이상 60자 이하여야 합니다.")
   @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
      message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
   )
   String newPassword
) {}
