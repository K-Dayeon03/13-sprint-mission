package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.command.CreateReadStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateReadStatusCommand;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(CreateReadStatusCommand command);
    ReadStatus findById(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId);
    ReadStatus update(UUID id, UpdateReadStatusCommand command);
    void deleteById(UUID id);
}
