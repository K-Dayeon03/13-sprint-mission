package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.command.UpdateMessageCommand;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sprint.mission.discodeit.entity.BinaryContent;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock MessageRepository messageRepository;
    @Mock ChannelRepository channelRepository;
    @Mock UserRepository userRepository;
    @Mock BinaryContentRepository binaryContentRepository;

    @InjectMocks BasicMessageService messageService;

    private User user;
    private Channel channel;
    private Message message;

    @BeforeEach
    void setUp() {
        user = new User("woody", "woody1234", "woody@codeit.com", null);
        channel = new Channel(ChannelType.PUBLIC, "공지", "설명", null);
        message = new Message("안녕하세요.", channel.getId(), user.getId());
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void create_success() {
        // given
        CreateMessageCommand request = new CreateMessageCommand(
                "안녕하세요.", channel.getId(), user.getId(), null);
        given(channelRepository.findById(channel.getId())).willReturn(channel);
        given(userRepository.findById(user.getId())).willReturn(user);
        given(messageRepository.save(any())).willReturn(message);

        // when
        Message result = messageService.create(request);

        // then
        assertThat(result.getContent()).isEqualTo("안녕하세요.");
        verify(messageRepository).save(any());
    }

    @Test
    @DisplayName("메시지 생성 실패 - 존재하지 않는 채널")
    void create_fail_channelNotFound() {
        // given
        CreateMessageCommand request = new CreateMessageCommand(
                "안녕하세요.", channel.getId(), user.getId(), null);
        given(channelRepository.findById(any())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> messageService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 채널");
    }

    @Test
    @DisplayName("메시지 생성 실패 - 존재하지 않는 유저")
    void create_fail_userNotFound() {
        // given
        CreateMessageCommand request = new CreateMessageCommand(
                "안녕하세요.", channel.getId(), user.getId(), null);
        given(channelRepository.findById(channel.getId())).willReturn(channel);
        given(userRepository.findById(any())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> messageService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 유저");
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void update_success() {
        // given
        UpdateMessageCommand request = new UpdateMessageCommand("수정된 내용");
        given(messageRepository.findById(message.getId())).willReturn(message);
        given(messageRepository.save(any())).willReturn(message);

        // when
        Message result = messageService.update(message.getId(), request);

        // then
        assertThat(result).isNotNull();
        verify(messageRepository).save(any());
    }

    @Test
    @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
    void update_fail_notFound() {
        // given
        UpdateMessageCommand request = new UpdateMessageCommand("수정된 내용");
        given(messageRepository.findById(any())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> messageService.update(UUID.randomUUID(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 메시지");
    }

    @Test
    @DisplayName("메시지 삭제 시 첨부파일 같이 삭제")
    void deleteById_cascadeDelete() {
        // given
        given(messageRepository.findById(message.getId())).willReturn(message);

        // when
        messageService.deleteById(message.getId());

        // then
        verify(binaryContentRepository).deleteAllByMessageId(message.getId());
        verify(messageRepository).deleteById(message.getId());
    }
    @Test
    @DisplayName("채널 ID로 메시지 목록 조회")
    void findAllByChannelId_success() {
        // given
        Message message2 = new Message("반갑습니다.", channel.getId(), user.getId());
        given(channelRepository.findById(channel.getId())).willReturn(channel);
        given(messageRepository.findByChannelId(channel.getId()))
                .willReturn(List.of(message, message2));

        // when
        List<Message> results = messageService.findAllByChannelId(channel.getId());

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting("content")
                .containsExactlyInAnyOrder("안녕하세요.", "반갑습니다.");
    }

    @Test
    @DisplayName("채널 ID로 메시지 목록 조회 실패 - 존재하지 않는 채널")
    void findAllByChannelId_fail_channelNotFound() {
        // given
        given(channelRepository.findById(channel.getId())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> messageService.findAllByChannelId(channel.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 채널");
        verify(messageRepository, never()).findByChannelId(any());
    }

    @Test
    @DisplayName("메시지 단건 조회 실패 - 존재하지 않는 메시지")
    void findById_fail_notFound() {
        // given
        given(messageRepository.findById(any())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> messageService.findById(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 메시지");
    }

    @Test
    @DisplayName("첨부파일과 함께 메시지 생성")
    void create_success_withAttachments() {
        // given
        List<BinaryContentCommand> attachments = List.of(
                new BinaryContentCommand("file1.png", "image/png", new byte[]{1, 2, 3}),
                new BinaryContentCommand("file2.png", "image/png", new byte[]{4, 5, 6})
        );
        CreateMessageCommand request = new CreateMessageCommand(
                "파일 첨부!", channel.getId(), user.getId(), attachments);

        BinaryContent attachment = new BinaryContent(null, message.getId(),
                "file1.png", "image/png", new byte[]{1, 2, 3});

        given(channelRepository.findById(channel.getId())).willReturn(channel);
        given(userRepository.findById(user.getId())).willReturn(user);
        given(messageRepository.save(any())).willReturn(message);
        given(binaryContentRepository.save(any())).willReturn(attachment);

        // when
        Message result = messageService.create(request);

        // then
        assertThat(result).isNotNull();
        verify(binaryContentRepository, times(2)).save(any()); // 첨부파일 2개 저장 확인
    }

    @Test
    @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지")
    void deleteById_fail_notFound() {
        // given
        given(messageRepository.findById(any())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> messageService.deleteById(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 메시지");
    }
}
