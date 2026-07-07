package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.dto.command.UpdateChannelCommand;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelResponse createPublic(CreatePublicChannelCommand command) {
        if (!StringUtils.hasText(command.name())) {
            throw new BadRequestException("채널명을 입력해주세요.");
        }
        Channel channel = new Channel(ChannelType.PUBLIC, command.name(),
                command.description(), null);
        channelRepository.save(channel);
        return ChannelResponse.from(channel, null, null);
    }

    @Override
    public ChannelResponse createPrivate(CreatePrivateChannelCommand command) {
        validateParticipantIds(command.participantIds());

        Channel channel = new Channel(ChannelType.PRIVATE, null, null, null);
        channelRepository.save(channel);

        List<UUID> participantIds = command.participantIds();
        participantIds.forEach(userId ->{
            ReadStatus readStatus = new ReadStatus(userId, channel.getId(), Instant.now());
            readStatusRepository.save(readStatus);
        });
        return ChannelResponse.from(channel, participantIds, null);
    }

    @Override
    public ChannelResponse findById(UUID id) {
        return toResponse(findChannelOrThrow(id));
    }

    //채널 -> ChannelResponse 변환 공통 메서드
    private ChannelResponse toResponse(Channel channel) {
        //가장 최근 메세지 시간 조회
        Instant lastMessageAt = messageRepository.findByChannelId(channel.getId())
                .stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
        //private 채널인 경우 참여자 id 목록 조회
        List<UUID> participantIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }
        return ChannelResponse.from(channel, participantIds, lastMessageAt);
    }
    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        Set<UUID> participatedPrivateChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        return channelRepository.findByAll().stream()
                .filter(channel -> {
                    if(channel.getType() == ChannelType.PUBLIC) {
                        return true; //전체조회
                    }
                    //참여한 채널만 조회
                    return participatedPrivateChannelIds.contains(channel.getId());
                })
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ChannelResponse update(UUID id, UpdateChannelCommand command) {
        Channel channel = findChannelOrThrow(id);
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new BadRequestException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        channel.update(
                channel.getType(),
                command.newName() != null ? command.newName() : channel.getName(),
                command.newDescription() != null ? command.newDescription() : channel.getDescription()
        );
        channelRepository.save(channel);
        return toResponse(channel);
    }



    @Override
    public void deleteById(UUID id) {
        findChannelOrThrow(id);
        MessageDeletionSupport.deleteByChannelId(messageRepository, binaryContentRepository, id);
        readStatusRepository.deleteByChannelId(id);
        channelRepository.deleteById(id);
    }

    private void validateParticipantIds(List<UUID> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            throw new BadRequestException("PRIVATE 채널 참여자는 1명 이상이어야 합니다.");
        }
        if (participantIds.stream().anyMatch(Objects::isNull)) {
            throw new BadRequestException("참여자 ID는 필수입니다.");
        }
        participantIds.stream()
                .filter(participantId -> Optional.ofNullable(userRepository.findById(participantId)).isEmpty())
                .findFirst()
                .ifPresent(participantId -> {
                    throw new NotFoundException("존재하지 않는 유저입니다.");
                });
    }

    private Channel findChannelOrThrow(UUID id) {
        return Optional.ofNullable(channelRepository.findById(id))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 채널입니다."));
    }
}
