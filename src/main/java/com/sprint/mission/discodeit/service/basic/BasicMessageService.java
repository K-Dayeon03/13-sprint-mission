package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.command.UpdateMessageCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(CreateMessageCommand command) {
        validateChannelExists(command.channelId());

        findUserOrThrow(command.authorId());

        Message message = new Message(command.content(), command.channelId(), command.authorId());
        messageRepository.save(message);

        if (command.attachments() != null && !command.attachments().isEmpty()) {
            command.attachments().forEach(attachmentCommand -> {
                BinaryContent attachment = new BinaryContent(
                        null,
                        message.getId(),
                        attachmentCommand.fileName(),
                        attachmentCommand.contentType(),
                        attachmentCommand.bytes()
                );
                BinaryContent savedAttachment = binaryContentRepository.save(attachment);
                message.addAttachmentId(savedAttachment.getId());
            });
            messageRepository.save(message);
        }

        return message;
    }

    @Override
    public Message findById(UUID id) {
        return Optional.ofNullable(messageRepository.findById(id))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 메시지입니다."));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        validateChannelExists(channelId);
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public Message update(UUID id, UpdateMessageCommand command) {
        Message message = findById(id);
        message.update(command.newContent());
        return messageRepository.save(message);
    }

    @Override
    public void deleteById(UUID id) {
        Message message = findById(id);
        MessageDeletionSupport.deleteById(messageRepository, binaryContentRepository, message);
    }

    private void validateChannelExists(UUID channelId) {
        Optional.ofNullable(channelRepository.findById(channelId))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 채널입니다."));
    }

    private void findUserOrThrow(UUID userId) {
        Optional.ofNullable(userRepository.findById(userId))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
    }
}
