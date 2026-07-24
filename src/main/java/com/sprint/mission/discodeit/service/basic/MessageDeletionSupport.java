package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.List;
import java.util.UUID;

final class MessageDeletionSupport {
    private MessageDeletionSupport() {
    }

    static void deleteById(
            MessageRepository messageRepository,
            BinaryContentRepository binaryContentRepository,
            Message message
    ) {
        messageRepository.deleteById(message.getId());
    }

    static void deleteByChannelId(
            MessageRepository messageRepository,
            BinaryContentRepository binaryContentRepository,
            UUID channelId
    ) {
        List<Message> messages = messageRepository.findByChannel_Id(channelId);
        messageRepository.deleteAll(messages);
    }

    static void deleteByAuthorId(
            MessageRepository messageRepository,
            BinaryContentRepository binaryContentRepository,
            UUID authorId
    ) {
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> authorId.equals(message.getAuthorId()))
                .toList();
        messageRepository.deleteAll(messages);
    }
}
