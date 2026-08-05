package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.dto.command.UpdateChannelCommand;
import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import org.springframework.stereotype.Component;

@Component
public class ChannelCommandMapper {

    public CreatePublicChannelCommand toCreatePublicCommand(CreatePublicChannelRequest request) {
        return new CreatePublicChannelCommand(request.name(), request.description());
    }

    public CreatePrivateChannelCommand toCreatePrivateCommand(CreatePrivateChannelRequest request) {
        return new CreatePrivateChannelCommand(request.name(), request.description(), request.participantIds());
    }

    public UpdateChannelCommand toUpdateCommand(UpdateChannelRequest request) {
        return new UpdateChannelCommand(request.newName(), request.newDescription());
    }
}
