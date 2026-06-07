package com.sprint.mission.discodeit.dto.request;

public record CreateBinaryContentRequest (
    String fileName,
    String contentType,
    byte[] bytes
){}