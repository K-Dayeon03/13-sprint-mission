package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

final class MessageDeletionSupport {
    private MessageDeletionSupport() {
    }

    static void deleteById(
            MessageRepository messageRepository,
            BinaryContentRepository binaryContentRepository,
            Message message
    ) {
        deleteAttachments(binaryContentRepository, List.of(message));
        messageRepository.deleteById(message.getId());
    }

    static void deleteByChannelId(
            MessageRepository messageRepository,
            BinaryContentRepository binaryContentRepository,
            UUID channelId
    ) {
        List<Message> messages = messageRepository.findByChannelId(channelId);
        deleteAttachments(binaryContentRepository, messages);
        messageRepository.deleteByChannelId(channelId);
    }

    static void deleteByAuthorId(
            MessageRepository messageRepository,
            BinaryContentRepository binaryContentRepository,
            UUID authorId
    ) {
        List<Message> messages = messageRepository.findByAll().stream()
                .filter(message -> Objects.equals(message.getAuthorId(), authorId))
                .toList();
        deleteAttachments(binaryContentRepository, messages);
        messageRepository.deleteByAuthorId(authorId);
    }

    private static void deleteAttachments(BinaryContentRepository binaryContentRepository, List<Message> messages) {
        messages.forEach(message -> binaryContentRepository.deleteAllByMessageId(message.getId()));
    }
}
