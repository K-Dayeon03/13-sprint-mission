package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private ChannelService channelService;
    private UserService userService;

    public JCFMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void init(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (channelService.findById(channelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        if (userService.findById(authorId) == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        // content 검증은 Message 생성자에서 하므로 제거

        Message message = new Message(content, channelId, authorId);
        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> findByAll() {
        return messageRepository.findByAll();
    }

    @Override
    public Message update(UUID id, String newContent) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }
        // newContent 검증은 message.update() 내부에서 하므로 제거

        message.update(newContent);
        return messageRepository.save(message);
    }

    @Override
    public void deleteById(UUID id) {
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