package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
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

//    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository) {
//        this.messageRepository = messageRepository;
//        this.channelRepository = channelRepository;
//        this.userRepository = userRepository;
//    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
            if (channelRepository.findById(channelId) == null) {
                throw new IllegalArgumentException("존재하지 않는 채널입니다.");
            }
            if (userRepository.findById(authorId) == null) {
                throw new IllegalArgumentException("존재하지 않는 유저입니다.");
            }
        //콘텐츠 검증은 생성에 위임

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
        return messageRepository.findByAll();
    }

    @Override
    public Message update(UUID id, String newContent) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }

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
