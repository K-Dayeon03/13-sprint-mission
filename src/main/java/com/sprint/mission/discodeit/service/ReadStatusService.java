package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreateReadStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateReadStatusCommand;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto create(CreateReadStatusCommand command);
    ReadStatusDto findById(UUID id);
    List<ReadStatusDto> findAllByUserId(UUID userId);
    ReadStatusDto update(UUID id, UpdateReadStatusCommand command);
    void deleteById(UUID id);
}
