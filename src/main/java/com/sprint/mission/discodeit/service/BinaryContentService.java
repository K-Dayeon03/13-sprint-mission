package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentCommand command);
    BinaryContentResponse findById(UUID id);
    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);
    void deleteById(UUID id);
}
