package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.command.CreateReadStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateReadStatusCommand;
import com.sprint.mission.discodeit.dto.request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateReadStatusRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReadStatusCommandMapper {

    public CreateReadStatusCommand toCreateCommand(CreateReadStatusRequest request) {
        return new CreateReadStatusCommand(request.userId(), request.channelId(), request.lastReadAt());
    }

    public CreateReadStatusCommand toCreateCommand(UUID userId, UUID channelId) {
        return new CreateReadStatusCommand(userId, channelId);
    }

    public UpdateReadStatusCommand toUpdateCommand(UpdateReadStatusRequest request) {
        return new UpdateReadStatusCommand(request.newLastReadAt());
    }
}
