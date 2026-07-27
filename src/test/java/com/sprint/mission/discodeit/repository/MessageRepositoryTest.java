package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class MessageRepositoryTest {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("채널 ID로 메시지 목록을 조회한다")
    void findAllByChannelIdAndCursor_success() {
        TestData data = saveTestData();

        List<Message> result = messageRepository.findAllByChannelIdAndCursor(
                data.channel().getId(),
                null,
                PageRequest.of(0, 10)
        );

        assertThat(result).extracting(Message::getContent)
                .containsExactly("third", "second", "first");
    }

    @Test
    @DisplayName("커서보다 오래된 메시지만 조회한다")
    void findAllByChannelIdAndCursor_withCursor() {
        TestData data = saveTestData();
        Instant cursor = data.secondMessage().getCreatedAt();

        List<Message> result = messageRepository.findAllByChannelIdAndCursor(
                data.channel().getId(),
                cursor,
                PageRequest.of(0, 10)
        );

        assertThat(result).extracting(Message::getContent)
                .containsExactly("first");
    }

    @Test
    @DisplayName("채널별 최신 메시지 시간을 조회한다")
    void findLastMessageAtByChannelIdIn_success() {
        TestData data = saveTestData();

        List<MessageRepository.ChannelLastMessageAt> result =
                messageRepository.findLastMessageAtByChannelIdIn(List.of(data.channel().getId()));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChannelId()).isEqualTo(data.channel().getId());
        assertThat(result.get(0).getLastMessageAt()).isEqualTo(data.thirdMessage().getCreatedAt());
    }

    @Test
    @DisplayName("메시지가 없는 채널은 최신 메시지 조회 결과에 포함되지 않는다")
    void findLastMessageAtByChannelIdIn_empty() {
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "empty", "empty channel", null));

        List<MessageRepository.ChannelLastMessageAt> result =
                messageRepository.findLastMessageAtByChannelIdIn(List.of(channel.getId()));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("메시지를 페이징과 정렬로 조회한다")
    void findAll_pageAndSort() {
        saveTestData();

        Page<Message> page = messageRepository.findAll(PageRequest.of(0, 2, Sort.by("createdAt").descending()));

        assertThat(page.getContent()).extracting(Message::getContent)
                .containsExactly("third", "second");
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 채널 ID로 조회하면 빈 목록을 반환한다")
    void findByChannelId_fail_empty() {
        List<Message> result = messageRepository.findByChannel_Id(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    private TestData saveTestData() {
        User author = userRepository.save(new User("woody", "password1", "woody@codeit.com", null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "general channel", null));

        Message first = new Message("first", channel, author);
        Message second = new Message("second", channel, author);
        Message third = new Message("third", channel, author);
        setCreatedAt(first, Instant.parse("2026-07-27T09:00:00Z"));
        setCreatedAt(second, Instant.parse("2026-07-27T09:01:00Z"));
        setCreatedAt(third, Instant.parse("2026-07-27T09:02:00Z"));

        messageRepository.saveAll(List.of(first, second, third));
        messageRepository.flush();

        return new TestData(channel, first, second, third);
    }

    private static void setCreatedAt(Message message, Instant createdAt) {
        ReflectionTestUtils.setField(message, "createdAt", createdAt);
    }

    private record TestData(
            Channel channel,
            Message firstMessage,
            Message secondMessage,
            Message thirdMessage
    ) {
    }
}
