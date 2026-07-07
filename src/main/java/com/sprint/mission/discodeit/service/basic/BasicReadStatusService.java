package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.CreateReadStatusCommand;
import com.sprint.mission.discodeit.dto.command.UpdateReadStatusCommand;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(CreateReadStatusCommand command) {
        Optional.ofNullable(userRepository.findById(command.userId()))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        Optional.ofNullable(channelRepository.findById(command.channelId()))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 채널입니다."));
        readStatusRepository.findByUserIdAndChannelId(command.userId(), command.channelId())
                .ifPresent(rs -> {
                    throw new BadRequestException("이미 존재하는 ReadStatus입니다.");
                });

        ReadStatus readStatus = new ReadStatus(command.userId(), command.channelId(), command.lastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus findById(UUID id) {
        return Optional.ofNullable(readStatusRepository.findById(id))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 ReadStatus입니다."));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID id, UpdateReadStatusCommand command) {
        ReadStatus readStatus = findById(id);
        readStatus.updateLastReadAt(command.newLastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        readStatusRepository.deleteById(id);
    }
}
