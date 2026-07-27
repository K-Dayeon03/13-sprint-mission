package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreateReadStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateReadStatusCommand;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusDto create(CreateReadStatusCommand command) {
        User user = findUserOrThrow(command.userId());
        Channel channel = findChannelOrThrow(command.channelId());
        readStatusRepository.findByUser_IdAndChannel_Id(command.userId(), command.channelId())
                .ifPresent(rs -> {
                    throw new BadRequestException("이미 존재하는 ReadStatus입니다.");
                });

        ReadStatus readStatus = new ReadStatus(user, channel, command.lastReadAt());
        return readStatusMapper.toDto(readStatusRepository.save(readStatus));
    }

    @Override
    public ReadStatusDto findById(UUID id) {
        return readStatusMapper.toDto(findEntityOrThrow(id));
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUser_Id(userId).stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ReadStatusDto update(UUID id, UpdateReadStatusCommand command) {
        ReadStatus readStatus = findEntityOrThrow(id);
        readStatus.updateLastReadAt(command.newLastReadAt());
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        findEntityOrThrow(id);
        readStatusRepository.deleteById(id);
    }

    private ReadStatus findEntityOrThrow(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 ReadStatus입니다."));
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
    }

    private Channel findChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 채널입니다."));
    }
}
