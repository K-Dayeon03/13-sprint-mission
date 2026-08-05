package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ChannelRepositoryTest {

    @Autowired
    ChannelRepository channelRepository;

    @Test
    @DisplayName("채널을 저장하고 ID로 조회한다")
    void findById_success() {
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "general channel", null));

        Optional<Channel> result = channelRepository.findById(channel.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("general");
        assertThat(result.get().getType()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("없는 ID로 채널을 조회하면 빈 Optional을 반환한다")
    void findById_fail_notFound() {
        Optional<Channel> result = channelRepository.findById(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널 목록을 이름 기준으로 페이징 정렬한다")
    void findAll_pageAndSort() {
        channelRepository.save(new Channel(ChannelType.PUBLIC, "notice", "notice channel", null));
        channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "general channel", null));
        channelRepository.save(new Channel(ChannelType.PUBLIC, "archive", "archive channel", null));

        Page<Channel> page = channelRepository.findAll(PageRequest.of(0, 2, Sort.by("name").ascending()));

        assertThat(page.getContent()).extracting(Channel::getName)
                .containsExactly("archive", "general");
        assertThat(page.hasNext()).isTrue();
    }
}
