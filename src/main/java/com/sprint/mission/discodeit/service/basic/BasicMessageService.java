package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.command.UpdateMessageCommand;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    @Transactional
    public MessageDto create(CreateMessageCommand command) {
        Channel channel = findChannelOrThrow(command.channelId());
        User author = findUserOrThrow(command.authorId());

        Message message = new Message(command.content(), channel, author);

        if (command.attachments() != null && !command.attachments().isEmpty()) {
            command.attachments().forEach(attachmentCommand -> {
                BinaryContent attachment = new BinaryContent(
                        null,
                        null,
                        attachmentCommand.fileName(),
                        attachmentCommand.contentType(),
                        attachmentCommand.bytes()
                );
                message.getAttachments().add(attachment);
            });
        }

        return MessageDto.from(messageRepository.save(message));
    }

    @Override
    public MessageDto findById(UUID id) {
        return MessageDto.from(findMessageOrThrow(id));
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        findChannelOrThrow(channelId);
        return messageRepository.findByChannel_Id(channelId).stream()
                .map(MessageDto::from)
                .toList();
    }

    @Override
    @Transactional
    public MessageDto update(UUID id, UpdateMessageCommand command) {
        Message message = findMessageOrThrow(id);
        message.update(command.newContent());
        return MessageDto.from(message);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Message message = findMessageOrThrow(id);
        MessageDeletionSupport.deleteById(messageRepository, binaryContentRepository, message);
    }

    private Message findMessageOrThrow(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 메시지입니다."));
    }

    private Channel findChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 채널입니다."));
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
    }
}
