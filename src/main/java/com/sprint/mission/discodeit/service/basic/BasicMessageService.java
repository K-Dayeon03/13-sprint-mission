package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
            if (channelRepository.findById(channelId) == null) {
                throw new IllegalArgumentException("존재하지 않는 채널입니다.");
            }
            if (userRepository.findById(authorId) == null) {
                throw new IllegalArgumentException("존재하지 않는 유저입니다.");
            }
            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("메세지 내용을 입력해주세요.");
            }

            Message message = new Message(content, channelId, authorId);
            return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        return  messageRepository.findById(id);
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> findByAll() {
        return messageRepository.findAll();
    }

    @Override
    public boolean update(UUID id, String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("메세지 내용을 입력해주세요.");
        }

        Message message = messageRepository.findById(id);

        if (message == null) {
            return false;  // 메시지 없으면 false 반환
        }

        message.update(newContent);
        messageRepository.save(message);
        return true;  // 성공 시 true 반환
    }

    @Override
    public void delete(UUID id) {
        messageRepository.deleteById(id);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        messageRepository.deleteByChannelId(channelId);
    }

    @Override
    public void deleteByAuthorId(UUID authorId) {
        messageRepository.deleteByAuthorId(authorId);
    }
}
