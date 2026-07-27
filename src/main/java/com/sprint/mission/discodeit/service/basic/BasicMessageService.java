package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.command.UpdateMessageCommand;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;
    @Override
    @Transactional
    public MessageDto create(CreateMessageCommand command) {
        int attachmentCount = command.attachments() == null ? 0 : command.attachments().size();
        log.debug("Creating message. channelId={}, authorId={}, attachmentCount={}",
                command.channelId(),
                command.authorId(),
                attachmentCount);

        Channel channel = findChannelOrThrow(command.channelId());
        User author = findUserOrThrow(command.authorId());

        Message message = new Message(command.content(), channel, author);

        List<BinaryContentCommand> attachmentCommands = command.attachments() == null
                ? List.of()
                : command.attachments();

        if (!attachmentCommands.isEmpty()) {
            attachmentCommands.forEach(attachmentCommand -> {
                BinaryContent attachment = new BinaryContent(
                        null,
                        null,
                        attachmentCommand.fileName(),
                        attachmentCommand.contentType(),
                        (long) attachmentCommand.bytes().length
                );
                message.getAttachments().add(attachment);
            });
        }

        Message saved = messageRepository.saveAndFlush(message);
        for (int i = 0; i < attachmentCommands.size(); i++) {
            BinaryContent attachment = saved.getAttachments().get(i);
            binaryContentStorage.put(attachment.getId(), attachmentCommands.get(i).bytes());
        }

        log.info("Message created. messageId={}, channelId={}, authorId={}, attachmentCount={}",
                saved.getId(), saved.getChannelId(), saved.getAuthorId(), attachmentCount);
        return messageMapper.toDto(saved);
    }

    @Override
    public MessageDto findById(UUID id) {
        Message message = findMessageOrThrow(id);
        return messageMapper.toDto(message);
    }

    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
        findChannelOrThrow(channelId);

        int pageSize = size <= 0 ? 50 : Math.min(size, 50);

        List<Message> messages = messageRepository.findAllByChannelIdAndCursor(
                channelId,
                cursor,
                PageRequest.of(0, pageSize + 1)
        );

        boolean hasNext = messages.size() > pageSize;

        List<Message> pageContent = hasNext
                ? messages.subList(0, pageSize)
                : messages;

        List<MessageDto> content = pageContent.stream()
                .map(messageMapper::toDto)
                .toList();

        Instant nextCursor = hasNext && !pageContent.isEmpty()
                ? pageContent.get(pageContent.size() - 1).getCreatedAt()
                : null;

        return pageResponseMapper.fromCursor(
                content,
                nextCursor,
                pageSize,
                hasNext
        );
    }
    @Override
    @Transactional
    public MessageDto update(UUID id, UpdateMessageCommand command) {
        log.debug("Updating message. messageId={}", id);

        Message message = findMessageOrThrow(id);
        message.update(command.newContent());

        log.info("Message updated. messageId={}", id);
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.debug("Deleting message. messageId={}", id);

        Message message = findMessageOrThrow(id);
        MessageDeletionSupport.deleteById(messageRepository, binaryContentRepository, message);

        log.info("Message deleted. messageId={}", id);
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
