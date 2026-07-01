package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(CreateMessageRequest request) {
        validateChannelExists(request.channelId());

        // 유저 존재 여부 확인
        if (userRepository.findById(request.authorId()) == null) {
            throw new NotFoundException("존재하지 않는 유저입니다.");
        }

        // 메시지 생성
        Message message = new Message(request.content(), request.channelId(), request.authorId());
        messageRepository.save(message);

        // 첨부파일 선택적 저장
        if (request.attachments() != null && !request.attachments().isEmpty()) {
            request.attachments().forEach(attachmentRequest -> {
                BinaryContent attachment = new BinaryContent(
                        null,               // userId는 null
                        message.getId(),    // messageId 설정
                        attachmentRequest.fileName(),
                        attachmentRequest.contentType(),
                        attachmentRequest.bytes()
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
        Message message = messageRepository.findById(id);
        if (message == null) {
            throw new NotFoundException("존재하지 않는 메시지입니다.");
        }
        return message;
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        validateChannelExists(channelId);
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public Message update(UUID id, UpdateMessageRequest request) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            throw new NotFoundException("존재하지 않는 메시지입니다.");
        }
        // newContent 검증은 message.update() 내부에 위임
        message.update(request.newContent());
        return messageRepository.save(message);
    }

    @Override
    public void deleteById(UUID id) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            throw new NotFoundException("존재하지 않는 메시지입니다.");
        }
        MessageDeletionSupport.deleteById(messageRepository, binaryContentRepository, message);
    }

    private void validateChannelExists(UUID channelId) {
        if (channelRepository.findById(channelId) == null) {
            throw new NotFoundException("존재하지 않는 채널입니다.");
        }
    }
}
