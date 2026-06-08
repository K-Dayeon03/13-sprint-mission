package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(CreateBinaryContentRequest request);
    BinaryContent findById(UUID id);
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    void deleteById(UUID id);
}