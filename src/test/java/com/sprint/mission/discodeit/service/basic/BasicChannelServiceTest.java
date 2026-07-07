package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.dto.command.UpdateChannelCommand;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock ChannelRepository channelRepository;
    @Mock MessageRepository messageRepository;
    @Mock ReadStatusRepository readStatusRepository;
    @Mock UserRepository userRepository;
    @Mock BinaryContentRepository binaryContentRepository;

    @InjectMocks BasicChannelService channelService;

    private Channel publicChannel;
    private Channel privateChannel;

    @BeforeEach
    void setUp() {
        publicChannel = new Channel(ChannelType.PUBLIC, "공지", "공지 채널입니다.", null);
        privateChannel = new Channel(ChannelType.PRIVATE, null, null, null);
    }

    @Test
    @DisplayName("PUBLIC 채널 생성 성공")
    void createPublic_success() {
        given(channelRepository.save(any())).willReturn(publicChannel);

        ChannelResponse response = channelService.createPublic(
                new CreatePublicChannelCommand("공지", "공지 채널입니다."));

        assertThat(response.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(response.name()).isEqualTo("공지");
    }

    @Test
    @DisplayName("PUBLIC 채널 생성 실패 - 채널명 없음")
    void createPublic_fail_emptyName() {
        assertThatThrownBy(() -> channelService.createPublic(
                new CreatePublicChannelCommand("", "설명")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("채널명");
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 성공")
    void createPrivate_success() {
        User user1 = new User("user1", "password1", "user1@codeit.com", null);
        User user2 = new User("user2", "password2", "user2@codeit.com", null);
        List<UUID> participantIds = List.of(user1.getId(), user2.getId());
        given(userRepository.findById(user1.getId())).willReturn(user1);
        given(userRepository.findById(user2.getId())).willReturn(user2);
        given(channelRepository.save(any())).willReturn(privateChannel);

        ChannelResponse response = channelService.createPrivate(
                new CreatePrivateChannelCommand(participantIds));

        assertThat(response.type()).isEqualTo(ChannelType.PRIVATE);
        verify(readStatusRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 실패 - 존재하지 않는 참여자")
    void createPrivate_fail_notFoundParticipant() {
        UUID participantId = UUID.randomUUID();
        given(userRepository.findById(participantId)).willReturn(null);

        assertThatThrownBy(() -> channelService.createPrivate(
                new CreatePrivateChannelCommand(List.of(participantId))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 유저");

        verify(channelRepository, never()).save(any());
        verify(readStatusRepository, never()).save(any());
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 실패 - 참여자 목록 없음")
    void createPrivate_fail_emptyParticipantIds() {
        assertThatThrownBy(() -> channelService.createPrivate(
                new CreatePrivateChannelCommand(List.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여자");

        verify(channelRepository, never()).save(any());
        verify(readStatusRepository, never()).save(any());
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 실패 - 참여자 ID가 null")
    void createPrivate_fail_nullParticipantId() {
        assertThatThrownBy(() -> channelService.createPrivate(
                new CreatePrivateChannelCommand(Collections.singletonList(null))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여자 ID");

        verify(channelRepository, never()).save(any());
        verify(readStatusRepository, never()).save(any());
    }

    @Test
    @DisplayName("PRIVATE 채널 수정 실패")
    void update_fail_privateChannel() {
        given(channelRepository.findById(privateChannel.getId())).willReturn(privateChannel);

        assertThatThrownBy(() -> channelService.update(privateChannel.getId(),
                new UpdateChannelCommand("새이름", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PRIVATE 채널은 수정할 수 없습니다");
    }

    @Test
    @DisplayName("채널 삭제 시 메시지, ReadStatus 같이 삭제")
    void deleteById_cascadeDelete() {
        Message message = new Message("첨부파일 있는 메시지", publicChannel.getId(), UUID.randomUUID());
        given(channelRepository.findById(publicChannel.getId())).willReturn(publicChannel);
        given(messageRepository.findByChannelId(publicChannel.getId())).willReturn(List.of(message));

        channelService.deleteById(publicChannel.getId());

        verify(binaryContentRepository).deleteAllByMessageId(message.getId());
        verify(messageRepository).deleteByChannelId(publicChannel.getId());
        verify(readStatusRepository).deleteByChannelId(publicChannel.getId());
        verify(channelRepository).deleteById(publicChannel.getId());
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
    void deleteById_fail_notFound() {
        given(channelRepository.findById(any())).willReturn(null);

        assertThatThrownBy(() -> channelService.deleteById(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 채널");

        verify(messageRepository, never()).deleteByChannelId(any());
        verify(readStatusRepository, never()).deleteByChannelId(any());
        verify(channelRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("채널 단건 조회 성공")
    void findById_success() {
        given(channelRepository.findById(publicChannel.getId())).willReturn(publicChannel);
        given(messageRepository.findByChannelId(publicChannel.getId())).willReturn(List.of());

        ChannelResponse response = channelService.findById(publicChannel.getId());

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(publicChannel.getId());
        assertThat(response.lastMessageAt()).isNull();
    }

    @Test
    @DisplayName("채널 단건 조회 실패 - 존재하지 않는 채널")
    void findById_fail_notFound() {
        given(channelRepository.findById(any())).willReturn(null);

        assertThatThrownBy(() -> channelService.findById(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 채널");
    }

    @Test
    @DisplayName("채널 수정 실패 - 존재하지 않는 채널")
    void update_fail_notFound() {
        given(channelRepository.findById(any())).willReturn(null);

        assertThatThrownBy(() -> channelService.update(UUID.randomUUID(),
                new UpdateChannelCommand("새이름", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 채널");
    }

    @Test
    @DisplayName("userId로 채널 목록 조회 - PUBLIC은 전체, PRIVATE은 참여한 것만")
    void findAllByUserId_success() {
        UUID userId = UUID.randomUUID();

        Channel myPrivateChannel = new Channel(ChannelType.PRIVATE, null, null, null);
        Channel otherPrivateChannel = new Channel(ChannelType.PRIVATE, null, null, null);

        ReadStatus myReadStatus = new ReadStatus(userId, myPrivateChannel.getId(), Instant.now());

        given(channelRepository.findByAll())
                .willReturn(List.of(publicChannel, myPrivateChannel, otherPrivateChannel));
        given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(myReadStatus));
        given(messageRepository.findByChannelId(publicChannel.getId())).willReturn(List.of());
        given(readStatusRepository.findAllByChannelId(myPrivateChannel.getId()))
                .willReturn(List.of(myReadStatus));
        given(messageRepository.findByChannelId(myPrivateChannel.getId())).willReturn(List.of());

        List<ChannelResponse> responses = channelService.findAllByUserId(userId);

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting("type")
                .containsExactlyInAnyOrder(ChannelType.PUBLIC, ChannelType.PRIVATE);
        verify(readStatusRepository).findAllByUserId(userId);
        verify(readStatusRepository, never()).findAllByChannelId(otherPrivateChannel.getId());
    }
}
