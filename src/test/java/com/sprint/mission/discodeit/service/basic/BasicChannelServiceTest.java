package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.dto.command.UpdateChannelCommand;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock ChannelRepository channelRepository;
    @Mock MessageRepository messageRepository;
    @Mock ReadStatusRepository readStatusRepository;
    @Mock UserRepository userRepository;
    @Mock BinaryContentRepository binaryContentRepository;
    @Mock ChannelMapper channelMapper;
    @Mock UserMapper userMapper;

    @InjectMocks BasicChannelService channelService;

    private Channel publicChannel;
    private Channel privateChannel;

    @BeforeEach
    void setUp() {
        publicChannel = new Channel(ChannelType.PUBLIC, "공지", "공지 채널입니다.", null);
        privateChannel = new Channel(ChannelType.PRIVATE, null, null, null);
        setId(publicChannel, UUID.randomUUID());
        setId(privateChannel, UUID.randomUUID());
    }

    @Test
    @DisplayName("PUBLIC 채널 생성 성공")
    void createPublic_success() {
        ChannelDto dto = new ChannelDto(publicChannel.getId(), ChannelType.PUBLIC, "공지", "공지 채널입니다.", null, null);

        given(channelRepository.save(any(Channel.class))).willAnswer(invocation -> {
            Channel saved = invocation.getArgument(0);
            setId(saved, publicChannel.getId());
            return saved;
        });
        given(messageRepository.findByChannel_Id(publicChannel.getId())).willReturn(List.of());
        given(channelMapper.toDto(any(Channel.class), eq(null), eq(null))).willReturn(dto);

        ChannelDto result = channelService.createPublic(new CreatePublicChannelCommand("공지", "공지 채널입니다."));

        assertThat(result).isEqualTo(dto);
        verify(channelRepository).save(any(Channel.class));
    }

    @Test
    @DisplayName("PUBLIC 채널 생성 실패 - 채널명 없음")
    void createPublic_fail_emptyName() {
        assertThatThrownBy(() -> channelService.createPublic(new CreatePublicChannelCommand("", "설명")))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("채널명");
        verify(channelRepository, never()).save(any());
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 성공")
    void createPrivate_success() {
        User user = new User("user1", "password1", "user1@codeit.com", null);
        setId(user, UUID.randomUUID());
        ReadStatus readStatus = new ReadStatus(user, privateChannel, Instant.now());
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, true);
        ChannelDto dto = new ChannelDto(privateChannel.getId(), ChannelType.PRIVATE, null, null, List.of(userDto), null);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(channelRepository.save(any(Channel.class))).willAnswer(invocation -> {
            Channel saved = invocation.getArgument(0);
            setId(saved, privateChannel.getId());
            return saved;
        });
        given(messageRepository.findByChannel_Id(privateChannel.getId())).willReturn(List.of());
        given(readStatusRepository.findAllByChannel_Id(privateChannel.getId())).willReturn(List.of(readStatus));
        given(userMapper.toDto(user)).willReturn(userDto);
        given(channelMapper.toDto(any(Channel.class), anyList(), eq(null))).willReturn(dto);

        ChannelDto result = channelService.createPrivate(new CreatePrivateChannelCommand(List.of(user.getId())));

        assertThat(result).isEqualTo(dto);
        verify(readStatusRepository).save(any(ReadStatus.class));
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 실패 - 존재하지 않는 참여자")
    void createPrivate_fail_notFoundParticipant() {
        UUID participantId = UUID.randomUUID();
        given(userRepository.findById(participantId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> channelService.createPrivate(new CreatePrivateChannelCommand(List.of(participantId))))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 유저");
        verify(channelRepository, never()).save(any());
    }

    @Test
    @DisplayName("채널 단건 조회 성공")
    void findById_success() {
        Instant lastMessageAt = Instant.parse("2026-07-24T10:00:00Z");
        ChannelDto dto = new ChannelDto(publicChannel.getId(), ChannelType.PUBLIC, "공지", "공지 채널입니다.", null, lastMessageAt);

        given(channelRepository.findById(publicChannel.getId())).willReturn(Optional.of(publicChannel));
        given(messageRepository.findByChannel_Id(publicChannel.getId()))
                .willReturn(List.of(messageAt(publicChannel, lastMessageAt)));
        given(channelMapper.toDto(publicChannel, null, lastMessageAt)).willReturn(dto);

        ChannelDto result = channelService.findById(publicChannel.getId());

        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("채널 수정은 변경 감지로 처리한다")
    void update_success() {
        ChannelDto dto = new ChannelDto(publicChannel.getId(), ChannelType.PUBLIC, "새이름", "새설명", null, null);

        given(channelRepository.findById(publicChannel.getId())).willReturn(Optional.of(publicChannel));
        given(messageRepository.findByChannel_Id(publicChannel.getId())).willReturn(List.of());
        given(channelMapper.toDto(publicChannel, null, null)).willReturn(dto);

        ChannelDto result = channelService.update(publicChannel.getId(), new UpdateChannelCommand("새이름", "새설명"));

        assertThat(result).isEqualTo(dto);
        verify(channelRepository, never()).save(any());
    }

    @Test
    @DisplayName("userId로 채널 목록 조회는 bulk 조회로 N+1을 줄인다")
    void findAllByUserId_usesBulkQueries() {
        UUID userId = UUID.randomUUID();
        User participant = new User("user1", "password1", "user1@codeit.com", null);
        setId(participant, userId);
        ReadStatus myReadStatus = new ReadStatus(participant, privateChannel, Instant.now());
        Instant lastMessageAt = Instant.parse("2026-07-24T10:00:00Z");
        UserDto participantDto = new UserDto(userId, "user1", "user1@codeit.com", null, true);
        ChannelDto publicDto = new ChannelDto(publicChannel.getId(), ChannelType.PUBLIC, "공지", "공지 채널입니다.", null, lastMessageAt);
        ChannelDto privateDto = new ChannelDto(privateChannel.getId(), ChannelType.PRIVATE, null, null, List.of(participantDto), null);

        given(readStatusRepository.findAllByUser_Id(userId)).willReturn(List.of(myReadStatus));
        given(channelRepository.findAll()).willReturn(List.of(publicChannel, privateChannel));
        given(messageRepository.findLastMessageAtByChannelIdIn(anyList()))
                .willReturn(List.of(lastMessage(publicChannel.getId(), lastMessageAt)));
        given(readStatusRepository.findAllByChannel_IdIn(any())).willReturn(List.of(myReadStatus));
        given(userMapper.toDto(participant)).willReturn(participantDto);
        given(channelMapper.toDto(publicChannel, null, lastMessageAt)).willReturn(publicDto);
        given(channelMapper.toDto(privateChannel, List.of(participantDto), null)).willReturn(privateDto);

        List<ChannelDto> result = channelService.findAllByUserId(userId);

        assertThat(result).containsExactly(publicDto, privateDto);
        verify(messageRepository).findLastMessageAtByChannelIdIn(anyList());
        verify(readStatusRepository).findAllByChannel_IdIn(any());
        verify(messageRepository, never()).findByChannel_Id(any());
        verify(readStatusRepository, never()).findAllByChannel_Id(any());
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteById_success() {
        given(channelRepository.findById(publicChannel.getId())).willReturn(Optional.of(publicChannel));
        given(messageRepository.findByChannel_Id(publicChannel.getId())).willReturn(List.of());

        channelService.deleteById(publicChannel.getId());

        verify(messageRepository).deleteAll(List.of());
        verify(readStatusRepository).deleteByChannel_Id(publicChannel.getId());
        verify(channelRepository).deleteById(publicChannel.getId());
    }

    private static MessageRepository.ChannelLastMessageAt lastMessage(UUID channelId, Instant lastMessageAt) {
        return new MessageRepository.ChannelLastMessageAt() {
            @Override
            public UUID getChannelId() {
                return channelId;
            }

            @Override
            public Instant getLastMessageAt() {
                return lastMessageAt;
            }
        };
    }

    private static com.sprint.mission.discodeit.entity.Message messageAt(Channel channel, Instant createdAt) {
        User user = new User("author", "password", "author@codeit.com", null);
        setId(user, UUID.randomUUID());
        com.sprint.mission.discodeit.entity.Message message =
                new com.sprint.mission.discodeit.entity.Message("content", channel, user);
        setId(message, UUID.randomUUID());
        ReflectionTestUtils.setField(message, "createdAt", createdAt);
        return message;
    }

    private static void setId(Object entity, UUID id) {
        ReflectionTestUtils.setField(entity, "id", id);
    }
}
