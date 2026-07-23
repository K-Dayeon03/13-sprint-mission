package com.sprint.mission.discodeit.dto.command;

public record BinaryContentCommand(
        String fileName,
        String contentType,
        byte[] bytes
) {
}
