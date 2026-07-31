package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.dto.command.UpdateMessageCommand;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock MessageRepository messageRepository;
    @Mock ChannelRepository channelRepository;
    @Mock UserRepository userRepository;
    @Mock BinaryContentRepository binaryContentRepository;
    @Mock MessageMapper messageMapper;
    @Mock BinaryContentStorage binaryContentStorage;
    @Spy PageResponseMapper pageResponseMapper;

    @InjectMocks BasicMessageService messageService;

    private User user;
    private Channel channel;
    private Message message;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        user = new User("woody", "woody1234", "woody@codeit.com", null);
        channel = new Channel(ChannelType.PUBLIC, "공지", "설명", null);
        message = new Message("안녕하세요.", channel, user);

        setId(user, UUID.randomUUID());
        setId(channel, UUID.randomUUID());
        setId(message, UUID.randomUUID());
        setCreatedAt(message, Instant.parse("2026-07-24T10:00:00Z"));

        messageDto = new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                channel.getId(),
                null,
                List.of()
        );
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void create_success() {
        CreateMessageCommand command = new CreateMessageCommand("안녕하세요.", channel.getId(), user.getId(), null);

        given(channelRepository.findById(channel.getId())).willReturn(Optional.of(channel));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(messageRepository.saveAndFlush(any(Message.class))).willAnswer(invocation -> {
            Message saved = invocation.getArgument(0);
            setId(saved, message.getId());
            return saved;
        });
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        MessageDto result = messageService.create(command);

        assertThat(result).isEqualTo(messageDto);
        verify(messageRepository).saveAndFlush(any(Message.class));
    }

    @Test
    @DisplayName("첨부파일과 함께 메시지 생성 시 파일 데이터는 Storage에 저장한다")
    void create_withAttachments_storesBytes() {
        byte[] bytes = new byte[]{1, 2, 3};
        CreateMessageCommand command = new CreateMessageCommand(
                "파일 첨부",
                channel.getId(),
                user.getId(),
                List.of(new BinaryContentCommand("file.png", "image/png", bytes))
        );

        given(channelRepository.findById(channel.getId())).willReturn(Optional.of(channel));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(messageRepository.saveAndFlush(any(Message.class))).willAnswer(invocation -> {
            Message saved = invocation.getArgument(0);
            setId(saved, message.getId());
            BinaryContent attachment = saved.getAttachments().get(0);
            setId(attachment, UUID.randomUUID());
            return saved;
        });
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        MessageDto result = messageService.create(command);

        assertThat(result).isEqualTo(messageDto);
        verify(binaryContentStorage).put(any(UUID.class), eq(bytes));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 존재하지 않는 채널")
    void create_fail_channelNotFound() {
        CreateMessageCommand command = new CreateMessageCommand("안녕하세요.", channel.getId(), user.getId(), null);

        given(channelRepository.findById(channel.getId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.create(command))
                .isInstanceOf(ChannelNotFoundException.class)
                .hasMessageContaining("존재하지 않는 채널");
        verify(messageRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("메시지 생성 실패 - 존재하지 않는 작성자")
    void create_fail_authorNotFound() {
        CreateMessageCommand command = new CreateMessageCommand("안녕하세요.", channel.getId(), user.getId(), null);

        given(channelRepository.findById(channel.getId())).willReturn(Optional.of(channel));
        given(userRepository.findById(user.getId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.create(command))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("존재하지 않는 사용자");
        verify(messageRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("커서 기반 메시지 목록 조회")
    void findAllByChannelId_cursorPagination() {
        Message olderMessage = new Message("이전 메시지", channel, user);
        setId(olderMessage, UUID.randomUUID());
        setCreatedAt(olderMessage, Instant.parse("2026-07-24T09:59:00Z"));

        MessageDto olderDto = new MessageDto(
                olderMessage.getId(),
                olderMessage.getCreatedAt(),
                olderMessage.getUpdatedAt(),
                olderMessage.getContent(),
                channel.getId(),
                null,
                List.of()
        );

        given(channelRepository.findById(channel.getId())).willReturn(Optional.of(channel));
        given(messageRepository.findAllByChannel_Id(eq(channel.getId()), any()))
                .willReturn(new SliceImpl<>(List.of(message, olderMessage)));
        given(messageMapper.toDto(message)).willReturn(messageDto);

        PageResponse<MessageDto> result = messageService.findAllByChannelId(channel.getId(), null, 1);

        assertThat(result.content()).containsExactly(messageDto);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isEqualTo(message.getCreatedAt());
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("채널 메시지 목록 조회 실패 - 존재하지 않는 채널")
    void findAllByChannelId_fail_channelNotFound() {
        UUID channelId = UUID.randomUUID();
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.findAllByChannelId(channelId, null, 50))
                .isInstanceOf(ChannelNotFoundException.class)
                .hasMessageContaining("존재하지 않는 채널");
        verify(messageRepository, never()).findAllByChannel_Id(any(), any());
        verify(messageRepository, never()).findAllByChannelIdAndCursor(any(), any(), any());
    }

    @Test
    @DisplayName("메시지 수정은 변경 감지로 처리하고 DTO를 반환한다")
    void update_success() {
        UpdateMessageCommand command = new UpdateMessageCommand("수정된 내용");
        MessageDto updatedDto = new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                "수정된 내용",
                channel.getId(),
                null,
                List.of()
        );

        given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(updatedDto);

        MessageDto result = messageService.update(message.getId(), command);

        assertThat(result.content()).isEqualTo("수정된 내용");
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
    void update_fail_notFound() {
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.update(messageId, new UpdateMessageCommand("수정된 내용")))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessageContaining("존재하지 않는 메시지");
    }

    @Test
    @DisplayName("메시지 조회 실패 - 존재하지 않는 메시지")
    void findById_fail_notFound() {
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.findById(messageId))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessageContaining("존재하지 않는 메시지");
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteById_success() {
        given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));

        messageService.deleteById(message.getId());

        verify(messageRepository).deleteById(message.getId());
    }

    @Test
    @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지")
    void deleteById_fail_notFound() {
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.deleteById(messageId))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessageContaining("존재하지 않는 메시지");
        verify(messageRepository, never()).deleteById(any());
    }

    private static void setId(Object entity, UUID id) {
        ReflectionTestUtils.setField(entity, "id", id);
    }

    private static void setCreatedAt(Object entity, Instant createdAt) {
        ReflectionTestUtils.setField(entity, "createdAt", createdAt);
    }
}
