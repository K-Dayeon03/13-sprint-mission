package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(CreateReadStatusRequest request) {
        // 유저 존재 여부 확인
        if (userRepository.findById(request.userId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        // 채널 존재 여부 확인
        if (channelRepository.findById(request.channelId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        // 같은 Channel + User 조합 중복 체크
        readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
                .ifPresent(rs -> {
                    throw new IllegalArgumentException("이미 존재하는 ReadStatus입니다.");
                });

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId(), Instant.now());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus findById(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id);
        if (readStatus == null) {
            throw new IllegalArgumentException("존재하지 않는 ReadStatus입니다.");
        }
        return readStatus;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID id, UpdateReadStatusRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(id);
        if (readStatus == null) {
            throw new IllegalArgumentException("존재하지 않는 ReadStatus입니다.");
        }
        readStatus.updateLastReadAt(request.lastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void deleteById(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id);
        if (readStatus == null) {
            throw new IllegalArgumentException("존재하지 않는 ReadStatus입니다.");
        }
        readStatusRepository.deleteById(id);
    }
}