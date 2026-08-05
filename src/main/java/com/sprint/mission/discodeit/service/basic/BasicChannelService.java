package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.dto.command.UpdateChannelCommand;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.InvalidRequestException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ChannelDto createPublic(CreatePublicChannelCommand command) {
        if (!StringUtils.hasText(command.name())) {
            throw new InvalidRequestException("채널명을 입력해주세요.");
        }
        log.debug("Creating public channel. name={}", command.name());
        Channel channel = new Channel(ChannelType.PUBLIC, command.name(),
                command.description(), null);
        Channel saved = channelRepository.save(channel);

        log.info("Public channel created. channelId={}, name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ChannelDto createPrivate(CreatePrivateChannelCommand command) {
        List<User> participants = validateParticipantIds(command.participantIds());
        String name = StringUtils.hasText(command.name()) ? command.name() : null;
        String description = StringUtils.hasText(command.description()) ? command.description() : null;
        log.debug("Creating private channel. name={}, participantCount={}", name, command.participantIds().size());

        Channel channel = new Channel(ChannelType.PRIVATE, name, description, null);
        channelRepository.save(channel);

        participants.forEach(user -> {
            ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
            readStatusRepository.save(readStatus);
        });
        log.info("Private channel created. channelId={}, name={}, participantCount={}",
                channel.getId(), name, participants.size());
        return toResponse(channel);
    }

    @Override
    public ChannelDto findById(UUID id) {
        return toResponse(findChannelOrThrow(id));
    }

    //채널 -> ChannelDto 변환 공통 메서드
    private ChannelDto toResponse(Channel channel) {
        Instant lastMessageAt = messageRepository.findByChannel_Id(channel.getId()).stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        List<UserDto> participants = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participants = readStatusRepository.findAllByChannel_Id(channel.getId()).stream()
                    .map(ReadStatus::getUser)
                    .map(userMapper::toDto)
                    .toList();
        }

        return channelMapper.toDto(channel, participants, lastMessageAt);
    }
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        findUserOrThrow(userId);

        Set<UUID> participatedPrivateChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        List<Channel> channels = channelRepository.findAll().stream()
                .filter(channel -> {
                    if(channel.getType() == ChannelType.PUBLIC) {
                        return true; //전체조회
                    }
                    //참여한 채널만 조회
                    return participatedPrivateChannelIds.contains(channel.getId());
                })
                .toList();

        if (channels.isEmpty()) {
            return List.of();
        }

        List<UUID> channelIds = channels.stream()
                .map(Channel::getId)
                .toList();

        Map<UUID, Instant> lastMessageAtByChannelId = messageRepository.findLastMessageAtByChannelIdIn(channelIds).stream()
                .collect(Collectors.toMap(
                        MessageRepository.ChannelLastMessageAt::getChannelId,
                        MessageRepository.ChannelLastMessageAt::getLastMessageAt
                ));

        Set<UUID> privateChannelIds = channels.stream()
                .filter(channel -> channel.getType() == ChannelType.PRIVATE)
                .map(Channel::getId)
                .collect(Collectors.toSet());

        Map<UUID, List<UserDto>> participantsByChannelId = privateChannelIds.isEmpty()
                ? Map.of()
                : readStatusRepository.findAllByChannel_IdIn(privateChannelIds).stream()
                        .collect(Collectors.groupingBy(
                                ReadStatus::getChannelId,
                                Collectors.mapping(readStatus -> userMapper.toDto(readStatus.getUser()), Collectors.toList())
                        ));

        return channels.stream()
                .map(channel -> channelMapper.toDto(
                        channel,
                        channel.getType() == ChannelType.PRIVATE
                                ? participantsByChannelId.getOrDefault(channel.getId(), List.of())
                                : null,
                        lastMessageAtByChannelId.get(channel.getId())
                ))
                .toList();
    }

    @Override
    @Transactional
    public ChannelDto update(UUID id, UpdateChannelCommand command) {
        Channel channel = findChannelOrThrow(id);
        if (channel.getType() == ChannelType.PRIVATE) {
            log.warn("Private channel update rejected. channelId={}", id);
            throw new PrivateChannelUpdateException(id);
        }
        log.debug("Updating channel. channelId={}", id);

        channel.update(
                channel.getType(),
                command.newName() != null ? command.newName() : channel.getName(),
                command.newDescription() != null ? command.newDescription() : channel.getDescription()
        );
        log.info("Channel updated. channelId={}", id);
        return toResponse(channel);
    }



    @Override
    @Transactional
    public void deleteById(UUID id) {
        findChannelOrThrow(id);
        log.debug("Deleting channel. channelId={}", id);

        MessageDeletionSupport.deleteByChannelId(messageRepository, binaryContentRepository, id);
        readStatusRepository.deleteByChannel_Id(id);
        channelRepository.deleteById(id);
        log.info("Channel deleted. channelId={}", id);
    }

    private List<User> validateParticipantIds(List<UUID> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            throw new InvalidRequestException("PRIVATE 채널 참여자는 1명 이상이어야 합니다.");
        }
        if (participantIds.stream().anyMatch(Objects::isNull)) {
            throw new InvalidRequestException("참여자 ID는 필수입니다.");
        }
        if (participantIds.stream().distinct().count() != participantIds.size()) {
            throw new InvalidRequestException("PRIVATE 채널 참여자는 중복될 수 없습니다.");
        }
        return participantIds.stream()
                .map(participantId -> userRepository.findById(participantId)
                        .orElseThrow(() -> new UserNotFoundException(participantId)))
                .toList();
    }

    private Channel findChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new ChannelNotFoundException(id));
    }

    private User findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
