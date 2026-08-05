package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(BinaryContentCommand command);
    BinaryContentDto findById(UUID id);
    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);
    void deleteById(UUID id);
}
