package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
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

import java.time.Instant;
import java.util.List;
import java.util.Objects;
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
    public ChannelResponse createPublic(CreatePublicChannelRequest request) {
        if(request.name() == null || request.name().isBlank()) {
            throw new BadRequestException("채널명을 입력해주세요.");
        }
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(),
                request.description(), null);
        channelRepository.save(channel);
        return ChannelResponse.from(channel, null, null);
    }

    @Override
    public ChannelResponse createPrivate(CreatePrivateChannelRequest request) {
        validateParticipantIds(request.participantIds());

        //private채널은 name, description 생략
        Channel channel = new Channel(ChannelType.PRIVATE, null, null, null);
        channelRepository.save(channel);

        //참여 유저별 ReadStatus 생성
        List<UUID> participantIds = request.participantIds();
        participantIds.forEach(userId ->{
            ReadStatus readStatus = new ReadStatus(userId, channel.getId(), Instant.now());
            readStatusRepository.save(readStatus);
        });
        return ChannelResponse.from(channel, participantIds, null);
    }

    @Override
    public ChannelResponse findById(UUID id) {
        Channel channel = channelRepository.findById(id);
        if(channel == null) {
            throw new NotFoundException("존재하지 않는 채널입니다.");
        }
        return toResponse(channel);
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
    public ChannelResponse update(UUID id, UpdateChannelRequest request) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new NotFoundException("존재하지 않는 채널입니다.");
        }
        // PRIVATE 채널은 수정 불가
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new BadRequestException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        channel.update(
                channel.getType(),
                request.newName() != null ? request.newName() : channel.getName(),
                request.newDescription() != null ? request.newDescription() : channel.getDescription()
        );
        channelRepository.save(channel);
        return toResponse(channel);
    }



    @Override
    public void deleteById(UUID id) {
        MessageDeletionSupport.deleteByChannelId(messageRepository, binaryContentRepository, id);
        // 관련 ReadStatus 삭제
        readStatusRepository.deleteByChannelId(id);
        // 채널 삭제
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
                .filter(participantId -> userRepository.findById(participantId) == null)
                .findFirst()
                .ifPresent(participantId -> {
                    throw new NotFoundException("존재하지 않는 유저입니다.");
                });
    }

}
