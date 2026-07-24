package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

/*
* 패스워드를 제외한 유저 정보와 온라인 상태를 포함하는 응답 DTO입니다.
* */
//service가 바깥으로 데이터를 내보낼때 쓰는 포장지
public record UserDto(
        UUID id,
        String username,
        String email,
        BinaryContentDto profile,
        Boolean online

) {}
