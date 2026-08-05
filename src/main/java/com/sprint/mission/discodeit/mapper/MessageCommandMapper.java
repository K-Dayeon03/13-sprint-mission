package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.command.UpdateMessageCommand;
import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageCommandMapper {
    private final BinaryContentCommandMapper binaryContentCommandMapper;

    public CreateMessageCommand toCreateCommand(CreateMessageRequest request) {
        return new CreateMessageCommand(
                request.content(),
                request.channelId(),
                request.authorId(),
                toBinaryContentCommandsFromRequests(request.attachments())
        );
    }

    public CreateMessageCommand toCreateCommand(CreateMessageRequest request, List<MultipartFile> attachments) {
        return new CreateMessageCommand(
                request.content(),
                request.channelId(),
                request.authorId(),
                toBinaryContentCommandsFromFiles(attachments)
        );
    }

    public UpdateMessageCommand toUpdateCommand(UpdateMessageRequest request) {
        return new UpdateMessageCommand(request.newContent());
    }

    private List<BinaryContentCommand> toBinaryContentCommandsFromRequests(List<CreateBinaryContentRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }
        return requests.stream()
                .map(binaryContentCommandMapper::toCommand)
                .filter(command -> command != null)
                .toList();
    }

    private List<BinaryContentCommand> toBinaryContentCommandsFromFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        return files.stream()
                .map(file -> binaryContentCommandMapper.toCommand(file, "attachment"))
                .filter(command -> command != null)
                .toList();
    }
}
