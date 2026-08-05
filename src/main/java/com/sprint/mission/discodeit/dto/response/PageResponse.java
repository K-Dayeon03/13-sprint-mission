package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record PageResponse<T> (
    //메세지 목록 조회 API만 페이지네이션 응답으로 바꾸는 작업
    //Slice로 만들 때는 totalElements를 null로 넣기
    //page로 만들 때만 전체 개수를 넣기
    List<T> content,
    Object nextCursor,
    int size,
    boolean hasNext,
    Long totalElements
){}
